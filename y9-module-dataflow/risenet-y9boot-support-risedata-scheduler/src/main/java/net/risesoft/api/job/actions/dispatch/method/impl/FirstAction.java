package net.risesoft.api.job.actions.dispatch.method.impl;

import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.dispatch.DispatchJobAction;
import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.job.actions.dispatch.method.AbstractDispatchAction;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;
import net.risesoft.api.utils.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;
import oshi.jna.platform.mac.SystemB;

import java.util.List;

/**
 * @Description : 永远第一个
 * @ClassName FirstAction
 * @Author lb
 * @Date 2022/9/14 17:27
 * @Version 1.0
 */
@Component("第一个")
public class FirstAction extends AbstractDispatchAction implements DispatchJobAction {


    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {
        LResult lResult = new LResult();
        try {

            List<ServiceInstance> instances = getService(job);
            Result result;
            Object value;

            try {
                result = executorActionManager.action(job, jobLog, jobContext.getArgs(), instances.get(0),jobContext,null).onSuccess((res) -> {
                    res = ObjectUtils.nullOf(res, "NULL");
                    taskExecutor.successJob(job, jobLog, "调度成功:" + res, res.toString(), jobContext);
                    lResult.end(new Object[]{res});
                }).onError((e) -> {
                    e.printStackTrace();
                    taskExecutor.endJob(job, jobLog, JobLog.ERROR, instances.get(0).getInstanceId() + "调度失败原因:" + e.getMessage(), jobContext);
                    lResult.end(new Object[]{e});
                });
            } catch (Exception e) {
                e.printStackTrace();
                taskExecutor.endJob(job, jobLog, JobLog.ERROR, instances.get(0).getInstanceId() + "调度失败原因:" + e.getMessage(), jobContext);
                lResult.end(new Object[]{e});
            }

        } catch (Exception e) {
            taskExecutor.endJob(job, jobLog, JobLog.ERROR, e.getMessage(), jobContext);
            lResult.end(new Object[]{e});
        }
        return lResult;
    }
}
