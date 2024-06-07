package net.risedata.jdbc.executor.update;

import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import net.risedata.jdbc.operation.Operation;

/**
 * 提供update静态方法
 * 
 * @author libo 2021年5月24日
 */
public class Update {
	private static UpdateExecutor UPDATE;

	public static void setUpdateExecutor(UpdateExecutor update) {
		UPDATE = update;
	}

	public static int updateById(@NotNull Object id, Map<String, Object> valueMap, Map<String, Operation> operation) {
		return UPDATE.updateById(id, valueMap, operation);
	}

	public static int updateById(@NotNull Object id, Map<String, Object> valueMap) {
		return UPDATE.updateById(id, valueMap);
	}

	public static int updateById(@NotNull Object id) {
		return UPDATE.updateById(id);
	}

	public static int update(@NotNull Object entiry, @NotNull List<String> wheres, Map<String, Object> valueMap,
			Map<String, Operation> operationMap) {
		return UPDATE.update(entiry, wheres, valueMap, operationMap);
	}

	public static int update(@NotNull Object entiry, @NotNull List<String> wheres, Map<String, Object> valueMap) {
		return UPDATE.update(entiry, wheres, valueMap);
	}

	public static int update(@NotNull Object entiry, @NotNull List<String> wheres) {
		return UPDATE.update(entiry, wheres);
	}

}
