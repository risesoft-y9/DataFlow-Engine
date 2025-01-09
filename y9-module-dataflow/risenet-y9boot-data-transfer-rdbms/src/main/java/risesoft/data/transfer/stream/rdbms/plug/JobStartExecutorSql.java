package risesoft.data.transfer.stream.rdbms.plug;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.job.JobStartHandle;
import risesoft.data.transfer.core.plug.Plug;

/**
 * 任务启动执行sql
 * 
 * @typeName JobStartExecutorSql
 * @date 2024年12月31日
 * @author lb
 */
public class JobStartExecutorSql extends ConfigFinedExecutorSqlPlug implements JobStartHandle ,Plug{

	public JobStartExecutorSql(ExecutorConfig config, JobContext jobContext) {
		super(config, jobContext);
	}

	@Override
	public void onJobStart(JobContext jobContext) {

		if (config.getLog().isInfo()) {
			super.config.getLog().info(this, "job start executor sql:" + config.getSql());
		}
		Object result = doSql();
		if (config.getLog().isDebug()) {
			super.config.getLog().debug(this, "job start executor sql result:" + result);
		}
	}

	@Override
	public boolean register(JobContext jobContext) {
		return true;
	}

}
