package risesoft.data.transfer.core.executor;

import java.util.Collection;
import java.util.List;

import risesoft.data.transfer.core.close.Closed;

/**
 * 可执行的任务队列执行的方式由执行器来定
 * 
 * @typeName Executor
 * @date 2023年12月4日
 * @author lb
 */
public interface ExecutorTaskQueue extends Closed {

	/**
	 * 执行一个任务
	 * 
	 * @param runnable
	 */
	void add(Object task);

	/**
	 * 添加批任务
	 * 
	 * @param task
	 */
	void addBatch(@SuppressWarnings("rawtypes") Collection task);

	/**
	 * 获取剩余的任务
	 * 
	 * @return
	 */
	Collection<Object> getResidue();

	/**
	 * 获取剩余任务大小
	 * 
	 * @return
	 */
	int getResidueSize();

	/**
	 * 设置内部具体执行器
	 * 
	 * @param executor
	 */
	void setExecutorFacoty(ExecutorFacotry executor);

	/**
	 * 启动 对于输入执行器时会将任务在执行之前addBatch 需要start后才能执行
	 */
	void start();

	/**
	 * 获取执行器 的数量
	 * 
	 * @return
	 */
	int getExecutorSize();

	/**
	 * 关闭
	 */
	void shutdown() throws Exception;

	/**
	 * 添加监听器
	 * 
	 * @param executorListener
	 */
	void setExecutorListener(ExecutorListener executorListener);

}
