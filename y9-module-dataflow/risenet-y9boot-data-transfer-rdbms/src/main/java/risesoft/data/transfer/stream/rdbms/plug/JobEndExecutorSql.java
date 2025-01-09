package risesoft.data.transfer.stream.rdbms.plug;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.job.JobEndHandle;
import risesoft.data.transfer.core.plug.Plug;

/**
 * 任务结束执行sql 无论任务是否成功和失败都会执行
 * 
 * @typeName JobEndExecutorSql
 * @date 2024年12月31日
 * @author lb
 */
public class JobEndExecutorSql extends ConfigFinedExecutorSqlPlug implements JobEndHandle,Plug {

	public JobEndExecutorSql(ExecutorConfig config, JobContext jobContext) {
		super(config, jobContext);
	}

	@Override
	public void onJobEnd(JobContext jobContext) {
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
