package risesoft.data.transfer.core.executor;

import risesoft.data.transfer.core.close.Closed;

/**
 * 执行器
 * 
 * @typeName Executor
 * @date 2023年12月5日
 * @author lb
 * @param <Task>
 */
public interface Executor extends Closed{
	/**
	 * 执行任务
	 * 
	 * @param task 任务
	 */
	void run(Object task);
}
