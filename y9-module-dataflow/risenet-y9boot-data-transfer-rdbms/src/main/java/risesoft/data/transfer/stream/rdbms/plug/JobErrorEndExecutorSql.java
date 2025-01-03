package risesoft.data.transfer.stream.rdbms.plug;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.statistics.State;

/**
 * 任务失败结束执行sql
 * 
 * @typeName JobErrorEndExecutorSql
 * @date 2024年12月31日
 * @author lb
 */
public class JobErrorEndExecutorSql extends JobEndExecutorSql {

	public JobErrorEndExecutorSql(ExecutorConfig config, JobContext jobContext) {
		super(config, jobContext);
	}

	@Override
	public void onJobEnd(JobContext jobContext) {
		if (jobContext.getCommunication().getState()==State.FAILED) {
			super.onJobEnd(jobContext);
		}
	}

}
