package net.risedata.jdbc.executor.delete.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.executor.delete.DeleteExecutor;
import net.risedata.jdbc.executor.jdbc.JDBC;
import net.risedata.jdbc.executor.jdbc.JdbcExecutor;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.utils.Sqlbuilder;

/**
 * 执行delete操作的默认执行操作类
 * 
 * @author libo 2021年2月10日
 */
public class DefaultDeleteExecutor extends JDBC implements DeleteExecutor {
	/**
	 * sql执行器
	 */
	private JdbcExecutor jdbcExecutor;

	public DefaultDeleteExecutor(JdbcExecutor jdbcExecutor) {
		this.jdbcExecutor = jdbcExecutor;
	}

	/**
	 * @return the jdbcExecutor
	 */
	public JdbcExecutor getJdbcExecutor() {
		return jdbcExecutor;
	}

	/**
	 * @param jdbcExecutor the jdbcExecutor to set
	 */
	public void setJdbcExecutor(JdbcExecutor jdbcExecutor) {
		this.jdbcExecutor = jdbcExecutor;
	}

	@Override
	public int delete(@NotNull Object entiry, Map<String, Object> valueMap, Map<String, Operation> operationMap) {
		BeanConfig bc = getConfig(entiry);
		StringBuilder sql = new StringBuilder();
		List<FieldConfig> fields = bc.getFieldlist();
		List<Object> args = new ArrayList<Object>();
		sql.append(bc.getDelSql());
		createWhereSql(bc, sql, fields, args, operationMap, createValueMap(entiry, valueMap, fields, sql));
		return jdbcExecutor.update(sql.toString(), args.toArray());
	}

	@Override
	public int delete(@NotNull Object entiry, Map<String, Object> valueMap) {
		return delete(entiry, valueMap, null);
	}

	@Override
	public int delete(@NotNull Object entiry) {
		return delete(entiry, null);
	}

	@Override
	public int deleteById(@NotNull Object entiry, Map<String, Object> valueMap) {
		BeanConfig bc = getConfig(entiry);
		StringBuilder sql = new StringBuilder();
		List<FieldConfig> fields = bc.getIdField();
		List<Object> args = new ArrayList<Object>();
		sql.append("delete from " + bc.getTableName());
		createWhereSql(bc, sql, fields, args, null, createValueMap(entiry, valueMap, fields, sql));
		return jdbcExecutor.update(sql.toString(), args);
	}

	@Override
	public int deleteById(@NotNull Object entiry) {
		return deleteById(entiry, null);
	}

	@Override
	public int deleteById(@NotNull Class<?> id, Object... ids) {
		BeanConfig bc = getConfig(id);
		List<FieldConfig> fields = bc.getIdField();
		Sqlbuilder sql = new Sqlbuilder("delete from " + bc.getTableName());
		for (FieldConfig fieldConfig : fields) {
			sql.where(fieldConfig.getColumn() + " = ? ");
		}
		return jdbcExecutor.update(sql.toString(), ids);
	}

	@Override
	public int deleteByIds(@NotNull Class<?> id, Object... ids) {
		BeanConfig bc = getConfig(id);
		List<FieldConfig> fields = bc.getIdField();
		Sqlbuilder sql = new Sqlbuilder("delete from " + bc.getTableName());
		sql.where(fields.get(0).getColumn() + " in ( ");
		for (int i = 0; i < ids.length; i++) {
			sql.getBuilder().append("?");
			if (i != ids.length - 1) {
				sql.getBuilder().append(",");
			}
		}
		sql.getBuilder().append(")");
		return jdbcExecutor.update(sql.toString(), ids);
	}

}
