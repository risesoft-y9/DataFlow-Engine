package net.risedata.jdbc.executor.insert;

import java.util.Collection;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * 执行Insert操作类的执行器
 * 
 * @author libo 2021年2月10日
 */
public interface InsertExecutor {
	/**
	 * 插入
	 * 
	 * @param entiry   插入的entiry or id
	 * @param valueMap 自定义的值
	 * @return 返回 0代表未插入 -1代表 前置的检查检查失败
	 */
	int insert(@NotNull Object entiry, Map<String, Object> valueMap);

	/**
	 * 插入
	 * 
	 * @param entiry 插入的entiry or id
	 * @return 返回 0代表未插入 -1代表 前置的检查检查失败
	 */
	int insert(@NotNull Object entiry);

	/**
	 * 批量插入
	 * 
	 * @param entirys
	 * @return
	 */
	int[] batchInsert(@NotNull Collection<?> entirys);
	/**
	 * 批量插入
	 * 
	 * @param entirys
	 * @return
	 */
	int[] batchInsert(@NotNull Collection<?> entirys,String tableName);

	/**
	 * 批量插入
	 * 
	 * @param tableName 表名
	 * @param values 值
	 * @return
	 */
	int[] batchInsert(String tableName, @NotNull Collection<Map<String, Object>> values);
}
