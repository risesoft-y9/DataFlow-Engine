package risesoft.data.transfer.core.executor;

/**
 * 执行器监听器
 * 
 * @typeName ExecutorListener
 * @date 2023年12月11日
 * @author lb
 */
public interface ExecutorListener {

	void onError(Throwable e);

	/**
	 * 任务消费结束触发的事件
	 */
	void taskEnd(Object task);

	/**
	 * 启动执行器
	 */
	void start();

	/**
	 * 一个任务启动
	 */
	void taskStart(Object task);
}
