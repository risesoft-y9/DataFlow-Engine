package net.risedata.jdbc.executor.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.risedata.jdbc.commons.TypeCheck;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.exception.ConfigException;
import net.risedata.jdbc.factory.BeanConfigFactory;
import net.risedata.jdbc.mapping.RowMapping;
import net.risedata.jdbc.operation.Operation;
import net.risedata.jdbc.utils.LCollection;
import net.risedata.jdbc.utils.Sqlbuilder;


/**
 * 提供 jdbc 等执行类的一些通用的方法
 * 
 * @author libo 2021年2月18日
 */
public class JDBC {
	public static final String SQL_KEY = "$SQL";

	/**
	 * 查询返回对应的类型
	 * 
	 * @param <T>
	 * @param jdbcExecutor
	 * @param sql
	 * @param args
	 * @param bc
	 * @param fcs
	 * @param returnrtpe
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T queryForObject(JdbcExecutor jdbcExecutor, String sql, Object[] args, BeanConfig bc,
			Collection<FieldConfig> fcs, Class<T> returnrtpe) {
		assert jdbcExecutor != null && returnrtpe != null : "param is null";
		if (returnrtpe == Map.class || Map.class.isAssignableFrom(returnrtpe)) {
			return (T) jdbcExecutor.queryForMap(sql, args);
		}
		if (TypeCheck.isBaseType(returnrtpe)) {
			return jdbcExecutor.queryForSimpleObject(sql, returnrtpe, args);
		}

		return jdbcExecutor.queryForObject(sql, new RowMapping<T>(bc, fcs), args);
	}

	/**
	 * 创建valueMap
	 * 
	 * @param entiry
	 * @param valueMap
	 * @param fields
	 * @return
	 */
	protected static Map<String, Object> createValueMap(Object entiry, Map<String, Object> valueMap,
			Collection<FieldConfig> fields, StringBuilder sql) {
		Map<String, Object> retMap = null;
		boolean isInstance = !((entiry instanceof Class<?>) || (entiry instanceof String));
		if (isInstance && valueMap != null) {
			retMap = valueMap;
		} else {
		
			retMap = new HashMap<>();
		}
		retMap.put(SQL_KEY, sql);
		if (isInstance) {
			for (FieldConfig fieldConfig : fields) {
				try {
					if (!retMap.containsKey(fieldConfig.getFieldName())) {						
						retMap.put(fieldConfig.getFieldName(), fieldConfig.getValueField().get(entiry));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		//-- valueMap index ++
		if (valueMap != null&&retMap!=valueMap) {
			retMap.putAll(valueMap);
		}
		return retMap;
	}

	/**
	 * 拿到一个id 的beanConfig
	 * 
	 * @param id
	 * @return
	 */
	protected static BeanConfig getConfig(Object entiry) {
		return BeanConfigFactory.getInstance(entiry);
	}

	/**
	 * 内部排序 排序操作 内部插入
	 * 
	 * @param operations 自定义的操作集合 不能为空
	 * @param configs    被排序的字段配置
	 */
	private static void sort(Map<String, Operation> operations, FieldConfig[] configs, BeanConfig bc) {
		FieldConfig temp = null;
		int index = 0;
		Integer oldIndex = 0;
		Set<String> set = operations.keySet();
		Map<String, Integer> indexMap = new HashMap<String, Integer>(configs.length);
		for (int i = 0; i < configs.length; i++) {
			indexMap.put(configs[i].getFieldName(), i);
		}
		for (String field : set) {
			index = operations.get(field).getOperate();
			if (index > -1) {
				oldIndex = indexMap.get(field);
				if (oldIndex==null) {
					throw new ConfigException(field+" 不存在!");
				}
				if (index >= configs.length) {// 大于在最后一个拿到之前的位置后与最后一个进行替换位置
					temp = configs[configs.length - 1];
					configs[configs.length - 1] = configs[oldIndex];
					configs[oldIndex] = temp;
				} else {// 找到自己元素后将位置替换
					temp = configs[index];
					configs[index] = configs[oldIndex];
					configs[oldIndex] = temp;
				}
			}
		}
	}

	/**
	 * 创建条件sql
	 * 
	 * @param config       配置
	 * @param sql          sqlbuild
	 * @param fs           fieldconfigs
	 * @param args         参数集合
	 * @param operationMap 操作map
	 * @param valueMap     值map
	 */
	protected static void createWhereSql(BeanConfig config, StringBuilder sql, Collection<FieldConfig> fs,
			List<Object> args, Map<String, Operation> operationMap, Map<String, Object> valueMap) {
		boolean isWhere = false;
		Map<String, Object> excludeKey = null;
		boolean isOperation = operationMap != null; // 存在 自定义操作
		int length = -1;
		if (isOperation) {
			excludeKey = new HashMap<>();
			FieldConfig[] fss = new FieldConfig[fs.size()];
			fs.toArray(fss);// 排序
			sort(operationMap, fss, config);
			fs = new LCollection<FieldConfig>(fss);
		}
		for (FieldConfig fieldConfig : fs) {
			Operation op = fieldConfig.getOperation(valueMap);
			if (isOperation) {
				if (excludeKey.containsKey(fieldConfig.getFieldName())) {// 存在需要排除的key则跳过此次
					continue;
				}
				if (operationMap.containsKey(fieldConfig.getFieldName())) {
					op = operationMap.get(fieldConfig.getFieldName());
				}
			}
			length = sql.length();
			if (op.where(fieldConfig, args, sql, valueMap, config, excludeKey)) {
				if (isWhere) {
					sql.insert(length, " AND ");
				} else {
					sql.insert(length, " WHERE ");
					isWhere = true;
				}
			}
		}
	}

	protected static Sqlbuilder createIdWhere(BeanConfig bc, Sqlbuilder sqlbuild, List<Object> args,
			Map<String, Object> valueMap) {
		List<FieldConfig> idfc = bc.getIdField();
		for (FieldConfig fieldConfig : idfc) {
			if (fieldConfig.isPlaceholder()) {
				continue;
			}
			sqlbuild.where(fieldConfig.getColumn() + " = ?");
			if (valueMap != null && valueMap.containsKey(fieldConfig.getFieldName())) {
				args.add(valueMap.get(fieldConfig.getFieldName()));
			}
		}
		return sqlbuild;
	}

	protected static Sqlbuilder createIdWhere(BeanConfig bc, Sqlbuilder sqlbuild) {
		List<FieldConfig> idfc = bc.getIdField();
		for (FieldConfig fieldConfig : idfc) {
			if (fieldConfig.isPlaceholder()) {
				continue;
			}
			sqlbuild.where(fieldConfig.getColumn() + " = ?");
		}
		return sqlbuild;
	}

	/**
	 * 根据id 检查是否存在
	 * 
	 * @param jdbcExecutor
	 * @param bc
	 * @param valueMap
	 * @param update
	 * @return
	 */
	protected static int check(JdbcExecutor jdbcExecutor, BeanConfig bc, Map<String, Object> valueMap, boolean update) {
		String checkSql = bc.getCheckSql();
		if (checkSql != null) {
			StringBuilder sql = new StringBuilder(checkSql);
			List<FieldConfig> checks = bc.getCheckedField();

			List<Object> args = new ArrayList<>();
			int length = sql.length();
			boolean isCheck = false;
			boolean flag = true;
			if (update) {
				for (FieldConfig field : bc.getIdField()) {
					isCheck = checkId(sql, valueMap, args, field);
					if (isCheck) {
						if (flag) {
							sql.insert(length, " WHERE ");
							flag = false;
						} else {
							sql.insert(length, " AND ");
						}
					}
					length = sql.length();
				}
			}
			for (FieldConfig field : checks) {
				isCheck = field.getDefaultOperation().check(sql, valueMap, args, field);
				if (isCheck) {
					if (flag) {
						sql.insert(length, " WHERE ");
						flag = false;
					} else {
						sql.insert(length, " AND ");
					}
				}
				length = sql.length();
			}

			Integer count = jdbcExecutor.queryForSimpleObject(sql.toString(), Integer.class, args.toArray());
			if (count != null && count > 0) {
				return -1;
			}
		}
		return 1;
	}

	private static boolean checkId(StringBuilder sql, Map<String, Object> valueMap, List<Object> args, FieldConfig fc) {
		Object value = valueMap.get(fc.getFieldName());
		if (value != null) {
			sql.append(fc.getColumn() + " != ?");
			args.add(value);
			return true;
		}
		return false;
	}

	protected static <T> T getMap(String key, Map<String, T> map, Class<T> T) {
		Object o = map.get(key);
		return o != null ? T.cast(o) : toException(key + "不存在该field映射", T);
	}

	protected static <T> T toException(String msg, Class<T> T) {
		throw new RuntimeException(msg);
	}
}
