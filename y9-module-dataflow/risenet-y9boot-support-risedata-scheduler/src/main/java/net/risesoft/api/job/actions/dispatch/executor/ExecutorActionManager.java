package net.risesoft.api.job.actions.dispatch.executor;

import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.dispatch.CountResult;
import net.risesoft.api.job.actions.dispatch.ExecutorAction;
import net.risesoft.api.job.creator.JobArgsCreator;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description : 调度步骤管理器
 * @ClassName DispatchAction
 * @Author lb
 * @Date 2022/9/14 17:17
 * @Version 1.0
 */
@Component
public class ExecutorActionManager implements ExecutorAction {
	
    @Autowired
    Map<String, ExecutorAction> executorJobActionMap;

    @Autowired
    TaskExecutorService taskExecutorService;

    @Autowired
    JobArgsCreator jobArgsCreator;

    @Override
    public Result action(Job job, JobLog jobLog, Map<String, Object> args, ServiceInstance iServiceInstance, JobContext jobContext, DoBalance doBalance) {
        String type = job.getType();

        ExecutorAction executorAction = executorJobActionMap.get(type);
        if (!StringUtils.isEmpty(job.getArgs())) {
            args.put("args", jobArgsCreator.creator(jobContext, job.getArgs()));
        }
        if (executorAction != null) {
            if (job.getErrorCount() != null && job.getErrorCount() > 0) {
                final ServiceInstance[] tempService = new ServiceInstance[]{iServiceInstance};
                
                return new CountResult(job.getErrorCount(), (count, err) -> {
                    if (doBalance != null&&err!=null) {
                        tempService[0] = doBalance.doBalance(count, tempService[0]);
                    }
                    taskExecutorService.appendSource(jobLog.getId(), tempService[0].getInstanceId(),
                            err != null ? ("异常信息" + err.getMessage() + "\n 调度失败开始重试:" + (job.getErrorCount() - count)) : "开始执行调度:" + type);

                    return executorAction.action(job, jobLog, args, tempService[0], jobContext, doBalance);
                });
            }
            taskExecutorService.appendSource(jobLog.getId(), iServiceInstance.getInstanceId(), "开始执行调度:" + type);
            return executorAction.action(job, jobLog, args, iServiceInstance, jobContext, doBalance);

        }
        throw new JobException("未知的调度类型:" + type);
    }
}
