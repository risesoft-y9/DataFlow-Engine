package net.risedata.jdbc.executor.delete;

import java.util.Map;

import javax.validation.constraints.NotNull;

import net.risedata.jdbc.operation.Operation;

/**
 * 删除提供静态方法</br>
 * 建议使用 {@link DeleteExecutor} 此静态类以过时
 * 
 * @author libo 2021年5月24日
 */
public class Delete {

	private static DeleteExecutor DELETE;

	public static void setDeleteExecutor(DeleteExecutor delete) {
		DELETE = delete;
	}

	public static int delete(@NotNull Object entiry, Map<String, Object> valueMap,
			Map<String, Operation> operationMap) {
		return DELETE.delete(entiry, valueMap, operationMap);
	}

	public static int delete(@NotNull Object entiry, Map<String, Object> valueMap) {
		return DELETE.delete(entiry, valueMap);
	}

	public static int delete(@NotNull Object entiry) {

		return DELETE.delete(entiry);
	}

	public static int deleteById(@NotNull Object entiry, Map<String, Object> valueMap) {
		return DELETE.deleteById(entiry, valueMap);
	}

	public static int deleteById(@NotNull Object entiry) {

		return DELETE.deleteById(entiry);
	}

	public static int deleteById(@NotNull Class<?> id, Object... ids) {

		return DELETE.deleteById(id, ids);
	}

}
