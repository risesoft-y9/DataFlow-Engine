package net.risedata.jdbc.executor.update;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import net.risedata.jdbc.operation.Operation;

/**
 * 执行update操作类的执行器
 * 
 * @author libo 2021年2月10日
 */
public interface UpdateExecutor {
	/**
	 * 根据id修改
	 * 
	 * @param id        修改的entiry 也就是id
	 * @param valueMap  格外的 值map
	 * @param operation 操作
	 * @return 修改的成功的条数
	 */
	int updateById(@NotNull Object id, Map<String, Object> valueMap, Map<String, Operation> operation);

	/**
	 * 根据id修改
	 * 
	 * @param id       修改的entiry 也就是id
	 * @param valueMap 格外的 值map
	 * @return 修改的成功的条数
	 */
	int updateById(@NotNull Object id, Map<String, Object> valueMap);

	/**
	 * 根据id修改
	 * 
	 * @param id 修改的entiry 也就是id
	 * @return 修改的成功的条数
	 */
	int updateById(@NotNull Object id);

	/**
	 * 自定义修改 自定义哪些字段是 where 其他的都是set
	 * 
	 * @param entiry       id entiry value
	 * @param wheres       自定义用于条件的部分
	 * @param valueMap     自定义的值value
	 * @param operationMap 自定义的操作map
	 * @return 修改成功的条数
	 */
	int update(@NotNull Object entiry, @NotNull List<String> wheres, Map<String, Object> valueMap,
			Map<String, Operation> operationMap);

	/**
	 * 自定义修改 自定义哪些字段是 where 其他的都是set
	 * 
	 * @param entiry   id entiry value
	 * @param wheres   自定义用于条件的部分
	 * @param valueMap 自定义的值value
	 * @return 修改成功的条数
	 */
	int update(@NotNull Object entiry, @NotNull List<String> wheres, Map<String, Object> valueMap);

	/**
	 * 自定义修改 自定义哪些字段是 where 其他的都是set
	 * 
	 * @param entiry id entiry value
	 * @param wheres 自定义用于条件的部分
	 * @return 修改成功的条数
	 */
	int update(@NotNull Object entiry, @NotNull List<String> wheres);

	/**
	 * 调用动态修改方法时候替换的表名
	 */
	public static final String DYNAMIC_UPDATE_TABLE_NAME = "$TABLENAME";

	/**
	 * 动态修改 主要是 对一些特定的字段做执行替换
	 * 
	 * @param sql  待执行的sql语句
	 * @param id   对应操作的类
	 * @param args 参数合集
	 * @return
	 */
	int dynamicUpdate(String sql, Class<?> id, Object... args);
}
