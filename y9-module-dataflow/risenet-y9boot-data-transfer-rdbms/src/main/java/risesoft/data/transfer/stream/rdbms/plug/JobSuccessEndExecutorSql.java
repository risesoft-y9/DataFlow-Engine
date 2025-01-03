package risesoft.data.transfer.stream.rdbms.plug;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.statistics.State;

/**
 * 任务成功结束执行sql
 * 
 * @typeName JobSuccessEndExecutorSql
 * @date 2024年12月31日
 * @author lb
 */
public class JobSuccessEndExecutorSql extends JobEndExecutorSql {

	public JobSuccessEndExecutorSql(ExecutorConfig config, JobContext jobContext) {
		super(config, jobContext);
	}

	@Override
	public void onJobEnd(JobContext jobContext) {
		if (jobContext.getCommunication().getState()==State.SUCCEEDED) {
			super.onJobEnd(jobContext);
		}
	}

}
