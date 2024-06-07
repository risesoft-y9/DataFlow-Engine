package risesoft.data.transfer.core.job;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.handle.Handle;

/**
 * 任务启动
 * 
 * @typeName JobStartHandle
 * @date 2023年12月29日
 * @author lb
 */
public interface JobStartHandle extends Handle{
	void onJobStart(JobContext jobContext);
}
