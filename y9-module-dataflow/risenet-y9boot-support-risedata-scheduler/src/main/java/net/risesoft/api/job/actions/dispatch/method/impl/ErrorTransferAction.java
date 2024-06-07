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
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description : 故障转移
 * @ClassName ErrorTransferAction
 * @Author lb
 * @Date 2022/9/14 17:27
 * @Version 1.0
 */
@Component("故障转移")
public class ErrorTransferAction extends AbstractDispatchAction implements DispatchJobAction {

    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {
        LResult lResult = new LResult();
        try {
            List<ServiceInstance> instances = getService(job);
            doOnTransfer(0, lResult, job, jobLog, taskExecutor, jobContext, instances);
        } catch (Exception e) {
            taskExecutor.endJob(job, jobLog, JobLog.ERROR, e.getMessage(), jobContext);
            lResult.end(new Object[]{e});
        }
        return lResult;
    }


    private LResult doOnTransfer(int index, LResult lResult, Job job, JobLog jobLog, TaskExecutorService taskExecutor,
                                 JobContext jobContext, List<ServiceInstance> instances) {
        try {

            executorActionManager.action(job, jobLog, jobContext.getArgs(), instances.get(index),jobContext,(count,ins)-> instances.get(instances.size()%(index+1+count))).onSuccess((res) -> {
                res = ObjectUtils.nullOf(res, "NULL");
                taskExecutor.successJob(job, jobLog, "调度成功:" + res, res.toString(), jobContext);
                lResult.end(new Object[]{res});
            }).onError((e) -> {
                doError(index, instances, job, jobLog, taskExecutor, jobContext, lResult, e);
            });
        } catch (Exception e) {
            doError(index, instances, job, jobLog, taskExecutor, jobContext, lResult, e);
        }
        return lResult;
    }


    private LResult doError(int index, List<ServiceInstance> instances, Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext, LResult lResult, Throwable e) {
        if ((index + 1) >= instances.size()) {
            taskExecutor.endJob(job, jobLog, JobLog.ERROR, instances.get(index).getInstanceId() + "调用报错无可转移服务", jobContext);
            lResult.end(new Object[]{e});
        } else {
            taskExecutor.appendLog(jobLog.getId(), instances.get(index).getInstanceId() + "调用报错开始转移至:" + instances.get(index + 1).getInstanceId());
            return doOnTransfer(index + 1, lResult, job, jobLog, taskExecutor, jobContext, instances);
        }
        return lResult;
    }
}
