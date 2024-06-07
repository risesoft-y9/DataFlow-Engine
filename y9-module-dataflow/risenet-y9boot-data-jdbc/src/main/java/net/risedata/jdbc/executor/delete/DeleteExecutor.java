package net.risedata.jdbc.executor.delete;

import java.util.Map;

import javax.validation.constraints.NotNull;

import net.risedata.jdbc.operation.Operation;

/**
 * 执行Delete操作类的执行器
 * 
 * @author libo 2021年2月10日
 */
public interface DeleteExecutor {
	/**
	 * 删除
	 * 
	 * @param entiry       id or 对象
	 * @param valueMap     自定义的value
	 * @param operationMap 自定义的操作
	 * @return
	 */
	int delete(@NotNull Object entiry, Map<String, Object> valueMap, Map<String, Operation> operationMap);

	/**
	 * 删除
	 * 
	 * @param entiry   id or 对象
	 * @param valueMap 自定义的value
	 * @return
	 */
	int delete(@NotNull Object entiry, Map<String, Object> valueMap);

	
	
	/**
	 * 删除
	 * 
	 * @param entiry id or 对象
	 * @return
	 */
	int delete(@NotNull Object entiry);

	/**
	 * 根据id删除
	 * @param entiry   id or 对象
	 * @param valueMap 自定义的value
	 * @return
	 */
	int deleteById(@NotNull Object entiry, Map<String, Object> valueMap);
	
	/**
	 * 根据id删除
	 * @param entiry   id or 对象
	 * @param valueMap 自定义的value
	 * @return
	 */
	int deleteById(@NotNull Object entiry);
	
	/**
	 * 根据id删除 id的值放在参数里面 注意 id的值必须与 entiry中的id对应且不能缺少
	 * @param entiry   id 
	 * @param ids id
	 * @return
	 */
	int deleteById(@NotNull Class<?> id,Object... ids);

	/**
	 * 根据id删选择的是第一个id
	 * @param entiry   id 
	 * @param ids id
	 * @return
	 */
	int deleteByIds(@NotNull Class<?> id,Object... ids);
}
