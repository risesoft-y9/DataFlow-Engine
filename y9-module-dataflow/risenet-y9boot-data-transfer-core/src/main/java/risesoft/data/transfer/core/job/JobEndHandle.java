package risesoft.data.transfer.core.job;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.handle.Handle;

/**
 * 任务结束
 * 
 * @typeName JobStartHandle
 * @date 2023年12月29日
 * @author lb
 */
public interface JobEndHandle extends Handle{

	void onJobEnd(JobContext jobContext);
}
