package net.risedata.jdbc.executor.insert.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotNull;
import org.apache.commons.lang3.StringUtils;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.executor.insert.InsertExecutor;
import net.risedata.jdbc.executor.jdbc.JDBC;
import net.risedata.jdbc.executor.jdbc.JdbcExecutor;
import net.risedata.jdbc.search.exception.NoValueException;

/**
 * 执行update操作的默认执行操作类
 * 
 * @author libo 2021年2月10日
 */
public class DefaultInsertExecutor extends JDBC implements InsertExecutor {
	/**
	 * sql执行器
	 */
	private JdbcExecutor jdbcExecutor;

	public DefaultInsertExecutor(JdbcExecutor jdbcExecutor) {
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

	/**
	 * 插入一个entiry
	 * 
	 * @param entiry   插入的实体类
	 * @param bc
	 * @param args
	 * @param sql
	 * @param valueMap
	 * @return
	 */
	private static int createInsertSql(BeanConfig bc, List<FieldConfig> fields, List<Object> args, StringBuilder sql,
			Map<String, Object> valueMap, boolean isAll) {
		List<String> columns = new ArrayList<>();// 插入的key也就是field
		for (FieldConfig fieldConfig : fields) {
			if (isAll) {
				columns.add(fieldConfig.getColumn());
			} else {
				fieldConfig.getDefaultOperation().insert(args, columns, valueMap, fieldConfig);
			}
		}
		sql.append("insert into " + bc.getTableName() + " (" + StringUtils.join(columns, ",") + ") values (");
		for (int i = 0; i < columns.size(); i++) {
			if (i != 0) {
				sql.append(",");
			}
			sql.append("?");
		}
		sql.append(")");
		return columns.size();
	}

	@Override
	public int insert(Object entiry, Map<String, Object> valueMap) {
		BeanConfig bc = getConfig(entiry);

		StringBuilder sql = new StringBuilder();
		valueMap = createValueMap(entiry, valueMap, bc.getAllFields(), sql);
		if (check(jdbcExecutor, bc, valueMap, false) == -1)
			return -1;
		List<Object> args = new ArrayList<Object>();
		createInsertSql(bc, bc.getAllFields(), args, sql, valueMap, false);
		return jdbcExecutor.update(sql.toString(), args.toArray());
	}

	@Override
	public int insert(Object entiry) {
		return insert(entiry, null);
	}

	@Override
	public int[] batchInsert(Collection<?> entirys, String tableName) {
		if (entirys.size() < 1) {
			return new int[] { -1 };
		}
		Object[] arr = entirys.toArray();
		Object entiry = arr[0];
		BeanConfig bc = getConfig(entiry);
		StringBuilder sql = new StringBuilder();
		List<Object> args = new ArrayList<>();
		List<FieldConfig> fields = bc.getFieldlist();
		Map<String, Object> valueMap = createValueMap(entiry, null, fields, sql);
		int size = createInsertSql(bc, fields, args, sql, valueMap, true);// 返回的是所需的size 以及构建sql
		List<Object[]> batchArgs2 = new ArrayList<>();
		for (Object object : arr) {
			args = new ArrayList<>();
			valueMap = createValueMap(object, null, fields, sql);
			for (FieldConfig fieldConfig : fields) {
				args.add(valueMap.get(fieldConfig.getFieldName()));
//				fieldConfig.getDefaultOperation().insert(args, null, valueMap, fieldConfig);
			}
			if (args.size() != size) {
				throw new NoValueException(
						"entiry " + object + " 缺少一个字段 或者超过没有设置一个字段的默认值 字段个数由 第一个entiry为准insert sql" + sql.toString());
			}
			batchArgs2.add(args.toArray());
		}

		int[] reset = jdbcExecutor
				.batchUpdate(StringUtils.isNotEmpty(tableName) ? sql.toString().replace(bc.getTableName(), tableName)
						: sql.toString(), batchArgs2);
		return reset;
	}

	@Override
	public int[] batchInsert(String tableName, @NotNull Collection<Map<String, Object>> values) {
		if (values == null || values.size() == 0) {
			return new int[] { -1 };
		}
		StringBuilder sql = new StringBuilder();
		Object[] keys = values.iterator().next().keySet().toArray();
		sql.append("insert into ").append(tableName).append("(");
		for (int i = 0; i < keys.length; i++) {
			sql.append(keys[i]);
			if (i != keys.length - 1) {
				sql.append(",");
			}
		}
		sql.append(") values (");
		for (int i = 0; i < keys.length; i++) {
			sql.append("?");
			if (i != keys.length - 1) {
				sql.append(",");
			}
		}
		sql.append(")");
		List<Object[]> args = new ArrayList<>();
		Object[] tempArgs;
		for (Map<String, Object> value : values) {
			tempArgs = new Object[keys.length];
			for (int i = 0; i < keys.length; i++) {
				tempArgs[i] = value.get(keys[i]);
			}
			args.add(tempArgs);
		}
		return jdbcExecutor.batchUpdate(sql.toString(), args);
	}

	@Override
	public int[] batchInsert(@NotNull Collection<?> entirys) {
		// TODO Auto-generated method stub
		return batchInsert(entirys, null);
	}

}
