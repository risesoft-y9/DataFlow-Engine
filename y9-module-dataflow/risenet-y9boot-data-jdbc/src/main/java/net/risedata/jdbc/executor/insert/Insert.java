package net.risedata.jdbc.executor.insert;

import java.util.Collection;
import java.util.Map;

import javax.validation.constraints.NotNull;

/**
 * 提供默认的insertExecutor 的静态实现</br>
 * 建议使用 {@link InsertExecutor} 此静态类已过时
 * 
 * @author libo 2021年5月24日
 */
public class Insert {

	private static InsertExecutor INSERT;

	public static void setInsertExecutor(InsertExecutor insert) {
		INSERT = insert;
	}

	public static int insert(@NotNull Object entiry, Map<String, Object> valueMap) {
		return INSERT.insert(entiry, valueMap);
	}

	public static int insert(@NotNull Object entiry) {
		return INSERT.insert(entiry);
	}

	public static int[] batchInsert(@NotNull Collection<?> entirys) {
		return INSERT.batchInsert(entirys);
	}

}
