package net.risesoft.api.job.actions.dispatch.executor.impl;

import net.risedata.rpc.provide.listener.SyncResult;
import net.risedata.rpc.provide.net.ClientConnection;
import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.actions.dispatch.ExecutorAction;
import net.risesoft.api.job.actions.dispatch.executor.DoBalance;
import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.job.actions.dispatch.executor.ResultError;
import net.risesoft.api.job.actions.dispatch.executor.ResultSuccess;
import net.risesoft.api.listener.ClientListener;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description : 用rpc 的方式执行
 * @ClassName JobExecutorAction
 * @Author lb
 * @Date 2022/9/15 11:12
 * @Version 1.0
 */
@Component("job")
public class JobExecutorAction implements ExecutorAction {
	
	/**
	 * 默认超时时间 120 秒当没配置超时时间的时候默认120秒
	 */
	@Value("${beta.job.timeOut:120}")
	private Integer defaultTimeOut;

	@Override
	public Result action(Job job, JobLog jobLog, Map<String, Object> args, ServiceInstance iServiceInstance,
			JobContext jobContext, DoBalance doBalance) {
		ClientConnection clinetConnection = ClientListener.getConnection(iServiceInstance.getInstanceId());
		if (clinetConnection == null) {
			throw new JobException(iServiceInstance.getInstanceId() + "调度失败未找到连接!");
		}

		SyncResult syncResult = clinetConnection.pushListener(job.getSource(), args,
				(job.getSourceTimeOut() > 0 ? job.getSourceTimeOut() : defaultTimeOut) * 1000);

		return new Result() {

			private List<ResultSuccess> successes = new ArrayList<>();
			private List<ResultError> errors = new ArrayList<>();
			private Object res;

			@Override
			public synchronized Result onSuccess(ResultSuccess success) {
				// TODO 后续需要优化改造
				if (this.res != null) {
					success.onSuccess(this.res);
					return this;
				}
				Object lockObject = this;
				successes.add(success);
				syncResult.onSuccess((res) -> {
					synchronized (lockObject) {
						this.res = res;
						for (int i = 0; i < successes.size(); i++) {
							if (res.getValue().size() == 1) {
								successes.remove(i).onSuccess(res.getValue().get(0));
								i--;
							} else {
								successes.remove(i).onSuccess(res.getValue());
								i--;
							}

						}
					}
				});
				return this;
			}

			@Override
			public Result onError(ResultError error) {
				errors.add(error);
				syncResult.onError((res, e) -> {
					for (int i = 0; i < errors.size(); i++) {
						errors.remove(i).onError(e);
						i--;
					}
				});
				return this;
			}

			@Override
			public Object getValue() {
				return syncResult.getResult().getValue();
			}
		};
	}
}
