package net.risesoft.api.job.actions.dispatch.method.impl;

import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.dispatch.DispatchJobAction;
import net.risesoft.api.job.actions.dispatch.method.AbstractDispatchAction;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;
import net.risesoft.api.utils.ObjectUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description : 永远第一个
 * @ClassName FirstAction
 * @Author lb
 * @Date 2022/9/14 17:27
 * @Version 1.0
 */
@Component("指定")
public class AssignAction extends AbstractDispatchAction implements DispatchJobAction {

    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {
        LResult lResult = new LResult();
        try {
            try {
                Object serverKey = jobContext.getArgs().get(TaskExecutorService.SERVER_KEY);
                List<ServiceInstance> instances = getService(job);
                for (ServiceInstance instance : instances) {
                    if (instance.getInstanceId().equals(serverKey)) {
                        executorActionManager.action(job, jobLog, jobContext.getArgs(), instance,jobContext,(count,ins)-> instance).onSuccess((res) -> {
                            res = ObjectUtils.nullOf(res, "NULL");
                            taskExecutor.successJob(job, jobLog, "调度成功:" + res, res.toString(), jobContext);
                            lResult.end(new Object[]{res});
                        }).onError((e) -> {
                            taskExecutor.endJob(job, jobLog, JobLog.ERROR, instance.getInstanceId() + "调度失败原因:" + e.getMessage(), jobContext);
                            lResult.end(new Object[]{e});
                        });
                        return lResult;
                    }
                }
                throw new JobException(serverKey + "服务不存在");

            } catch (Exception e) {
                taskExecutor.endJob(job, jobLog, JobLog.ERROR, "调度失败原因:" + e.getMessage(), jobContext);
                lResult.end(new Object[]{e});
            }

        } catch (Exception e) {
            taskExecutor.endJob(job, jobLog, JobLog.ERROR, e.getMessage(), jobContext);
            lResult.end(new Object[]{e});
        }
        return lResult;
    }
}
