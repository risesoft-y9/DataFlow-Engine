package net.risedata.jdbc.executor.update.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.executor.jdbc.JDBC;
import net.risedata.jdbc.executor.jdbc.JdbcExecutor;
import net.risedata.jdbc.executor.update.UpdateExecutor;
import net.risedata.jdbc.factory.BeanConfigFactory;
import net.risedata.jdbc.operation.Operation;

/**
 * 执行update操作的默认执行操作类
 * 
 * @author libo 2021年2月10日
 */
public class DefaultUpdateExecutor extends JDBC implements UpdateExecutor {
	/**
	 * sql执行器
	 */
	private JdbcExecutor jdbcExecutor;

	public DefaultUpdateExecutor(JdbcExecutor jdbcExecutor) {
		this.jdbcExecutor = jdbcExecutor;
	}

	private static void createUpdateSql(StringBuilder sql, Collection<FieldConfig> fieldList, List<Object> args,
			Map<String, Object> valueMap) {
		sql.append(" SET");
		for (FieldConfig fieldConfig : fieldList) {
			if (!fieldConfig.isId() && fieldConfig.getDefaultOperation().update(fieldConfig, args, sql, valueMap)) {
				sql.append(",");
			}
		}
		sql.delete(sql.length() - 1, sql.length());
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
	public int updateById(@NotNull Object id, Map<String, Object> valueMap, Map<String, Operation> operation) {
		BeanConfig bc = getConfig(id);
		StringBuilder sql = new StringBuilder(50);
		sql.append("update " + bc.getTableName());
		List<Object> args = new ArrayList<Object>();
		valueMap = createValueMap(id, valueMap, bc.getAllFields(), sql);
		if (bc.getCheckedField()!=null&&bc.getCheckedField().size()>0&&check(jdbcExecutor, bc, valueMap, true) == -1) {
			return -1;
		}
		createUpdateSql(sql, bc.getAllFields(), args, valueMap);
		List<FieldConfig> fc = new ArrayList<>();
		for (FieldConfig fieldConfig : bc.getIdField()) {
			if (fieldConfig.isPlaceholder()) {// 占位id
				if (operation != null && operation.containsKey(fieldConfig.getFieldName())) {
					fc.add(fieldConfig);
				}
				continue;
			}
			fc.add(fieldConfig);
		}
		createWhereSql(bc, sql, fc, args, operation, valueMap);
		return jdbcExecutor.update(sql.toString(), args.toArray());
	}

	@Override
	public int updateById(@NotNull Object id, Map<String, Object> valueMap) {
		return updateById(id, valueMap, null);
	}

	@Override
	public int updateById(@NotNull Object id) {
		return updateById(id, null);
	}

	@Override
	public int update(@NotNull Object entiry, @NotNull List<String> wheres, Map<String, Object> valueMap,
			Map<String, Operation> operationMap) {
		BeanConfig bc = getConfig(entiry);
		List<FieldConfig> whereField = new ArrayList<>();
		for (String where : wheres) {
			whereField.add(getMap(where, bc.getFields(), FieldConfig.class));
		}
		HashSet<FieldConfig> setField = new HashSet<>(bc.getFieldlist());
		setField.removeAll(whereField);
		StringBuilder sql = new StringBuilder("update " + bc.getTableName());
		List<Object> args = new ArrayList<>();
		valueMap = createValueMap(entiry, valueMap, bc.getFieldlist(), sql);
		createUpdateSql(sql, setField, args, valueMap);
		createWhereSql(bc, sql, whereField, args, operationMap, valueMap);
		return jdbcExecutor.update(sql.toString(), args.toArray());
	}

	@Override
	public int update(@NotNull Object entiry, @NotNull List<String> wheres, Map<String, Object> valueMap) {
		return update(entiry, wheres, valueMap, null);
	}

	@Override
	public int update(@NotNull Object entiry, @NotNull List<String> wheres) {
		return update(entiry, wheres, null);
	}

	@Override
	public int dynamicUpdate(String sql, Class<?> id, Object... args) {
		BeanConfig bc = BeanConfigFactory.getInstance(id);
		if (bc != null) {
			if (sql.contains(UpdateExecutor.DYNAMIC_UPDATE_TABLE_NAME)) {
				sql = sql.replace(UpdateExecutor.DYNAMIC_UPDATE_TABLE_NAME, bc.getTableName());
			}
		}
		return jdbcExecutor.update(sql, args);
	}

}
