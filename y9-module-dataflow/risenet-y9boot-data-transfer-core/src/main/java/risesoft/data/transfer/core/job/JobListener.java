package risesoft.data.transfer.core.job;

import risesoft.data.transfer.core.context.JobContext;

/**
 * 任务执行时的监听器
 * 
 * @typeName JobListener
 * @date 2023年12月11日
 * @author lb
 */
public interface JobListener {
	/**
	 * 当有异常时触发
	 * 
	 * @param e
	 */
	void onError(Throwable e);

	/**
	 * 失败触发
	 * 
	 * @param jobContext
	 */
	void onFailed(JobContext jobContext);

	/**
	 * 任务执行成功触发
	 * 
	 * @param jobContext
	 */
	void onSuccess(JobContext jobContext);
}
