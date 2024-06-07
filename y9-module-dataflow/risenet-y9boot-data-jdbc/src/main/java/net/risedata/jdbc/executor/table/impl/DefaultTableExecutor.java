package net.risedata.jdbc.executor.table.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.risedata.jdbc.commons.LForEach;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.exception.ConfigException;
import net.risedata.jdbc.executor.jdbc.JDBC;
import net.risedata.jdbc.executor.jdbc.JdbcExecutor;
import net.risedata.jdbc.executor.table.TableExecutor;
import net.risedata.jdbc.factory.BeanConfigFactory;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.search.Operations;
import net.risedata.jdbc.search.Order;
import net.risedata.jdbc.search.exception.FieldException;
import net.risedata.jdbc.table.TableConfig;
import net.risedata.jdbc.table.TableField;
import net.risedata.jdbc.type.Types;

public class DefaultTableExecutor extends JDBC implements TableExecutor {
	private JdbcExecutor jdbcExecutor;

	public DefaultTableExecutor(JdbcExecutor jdbcExecutor) {
		super();
		this.jdbcExecutor = jdbcExecutor;
	}

	@Override
	public boolean hasTable(String tableName, String findTable) {
		Integer table = jdbcExecutor.queryForObject(String.format(findTable, tableName.toUpperCase()), Integer.class);
		return table > 0;
	}

	@Override
	public boolean deleteTable(String tableName, String findTable) {
		String drop = " drop table " + tableName;
		if (hasTable(tableName, findTable)) {
			jdbcExecutor.update(drop);
			return true;
		}
		return false;
	}

	@Override
	public boolean updateTableName(String ovlTableName, String newName, String findTable) {
		String sql = "ALTER TABLE " + ovlTableName + " RENAME TO " + newName;
		if (hasTable(ovlTableName, findTable)) {
			jdbcExecutor.update(sql);
			return true;
		}
		return false;
	}

	@Override
	public boolean createTable(String findStr, TableConfig tc) {
		boolean has = deleteTable(tc.getTableName(), findStr);
		List<String> sqls = new ArrayList<String>();
		StringBuilder sql = new StringBuilder(" create table " + tc.getTableName() + "( ");
		List<TableField> tableFields = tc.getFields();
		for (int i = 0; i < tableFields.size(); i++) {
			sql.append(createFieldString(tableFields.get(i)));
			if (i != tableFields.size() - 1) {
				sql.append(" , ");
			} else {
				sql.append(" )");
			}
		}
		sqls.add(sql.toString());
		for (TableField tableField : tableFields) {
			if (StringUtils.isNotBlank(tableField.getAnnotation())) {
				sql = new StringBuilder("comment on column " + tc.getTableName() + "." + tableField.getFieldName()
						+ " is '" + tableField.getAnnotation() + "'");
				sqls.add(sql.toString());
			}
		}
		String[] sqlstr = new String[sqls.size()];
		jdbcExecutor.batchUpdate(sqls.toArray(sqlstr));
		return has;
	}

	@Override
	public void addConfig(TableConfig tc, String id, Map<String, Operation> operations, List<Order> orders) {
		if (StringUtils.isBlank(tc.getTableName()) || tc.getFields() == null || tc.getFields().size() < 1) {
			throw new ConfigException(id + "null table or fields ?");
		}
		BeanConfig bc = new BeanConfig();
		bc.setCountTableSql("select count(1) from " + tc.getTableName());
		bc.setSelectTableSql(
				"select " + TableConfig.TABLE_AS + ".* from " + tc.getTableName() + " " + TableConfig.TABLE_AS);
		bc.setDelSql("delete from " + tc.getTableName());
		bc.setTableName(tc.getTableName());
		if (orders != null) {
			for (Order o : orders) {
				bc.addOrder(o);
			}
		}
		FieldConfig fc = null;
		Operation o = null;
		List<TableField> tfs = tc.getFields();
		for (TableField tf : tfs) {
			fc = new FieldConfig();
			o = operations == null ? Operations.getSqlTypeDefaultOperation(tf.getType()) : null;
			if (o == null) {
				o = operations.get(tf.getFieldName()) == null ? Operations.getSqlTypeDefaultOperation(tf.getType())
						: operations.get(tf.getFieldName());
			}
			fc.setColumn(tf.getFieldName());
			fc.setDefaultOperation(o);
			fc.setId(tf.isKey());
			fc.setFieldType(Types.getSqlType(tf.getType()));
			fc.setFieldName(tf.getFieldName());
			bc.sort();
			bc.putField(tf.getFieldName(), fc);
		}
		BeanConfigFactory.putBeanConfig(id, bc);

	}

	@Override
	public void tableTransfer(String tableName, String transferName, Map<String, String> keyMapping, String where) {
		final String[] into = new String[keyMapping.keySet().size()];
		final String[] values = new String[keyMapping.keySet().size()];

		LForEach.ForEachUp(keyMapping.keySet(), (String key, int index) -> {
			into[index] = key;
			values[index] = keyMapping.get(key);
		});
		String sql = "insert into " + tableName + "(" + StringUtils.join(into, ",") + ")  ( select "
				+ StringUtils.join(values, ",") + " from " + transferName + (where == null ? "" : where) + ")";
		jdbcExecutor.update(sql);
	}

	private static String createFieldString(TableField tf) {
		StringBuilder sb = new StringBuilder(tf.getFieldName());
		if (StringUtils.isBlank(tf.getType()) || StringUtils.isBlank(tf.getFieldName())) {
			throw new FieldException(tf + " 没有指定类型或者名字 ");
		}
		sb.append("  " + tf.getType());
		if (tf.isKey()) {
			sb.append(" PRIMARY key ");
		} else {
			if (tf.isRequired()) {
				sb.append(" not null ");
			} else if (StringUtils.isNotBlank(tf.getDefaultExpression())) {
				sb.append(" default(" + tf.getDefaultExpression() + ")");
			}
		}
		return sb.toString();
	}
}
