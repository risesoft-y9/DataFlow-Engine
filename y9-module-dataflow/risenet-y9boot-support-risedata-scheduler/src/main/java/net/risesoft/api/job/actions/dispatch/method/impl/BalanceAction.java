package net.risesoft.api.job.actions.dispatch.method.impl;

import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.dispatch.DispatchJobAction;
import net.risesoft.api.job.actions.dispatch.method.AbstractDispatchAction;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;
import net.risesoft.api.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description : 均衡 获取空闲的服务
 * @ClassName BalanceAction
 * @Author lb
 * @Date 2022/9/14 17:27
 * @Version 1.0
 */
@Component("均衡")
public class BalanceAction extends AbstractDispatchAction implements DispatchJobAction {

    @Autowired
    JobLogService jobLogService;




    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {
        LResult lResult = new LResult();
        try {
            List<ServiceInstance> instances = getService(job);
            executorActionManager.action(job, jobLog, jobContext.getArgs(), jobLogService.getRunableMinInstance(instances),jobContext,(count,serviceInstance)->{

                return  jobLogService.getRunableMinInstance(instances);
            }).onSuccess((res) -> {
                res = ObjectUtils.nullOf(res, "NULL");
                taskExecutor.successJob(job, jobLog, "调度成功:" + res, res.toString(), jobContext);
                lResult.end(new Object[]{res});
            }).onError((e) -> {
                taskExecutor.endJob(job, jobLog, JobLog.ERROR, e.getMessage(), jobContext);
                lResult.end(new Object[]{e});
            });
        } catch (Exception e) {
            e.printStackTrace();
            taskExecutor.endJob(job, jobLog, JobLog.ERROR, e.getMessage(), jobContext);
            lResult.end(new Object[]{e});
        }
        return lResult;
    }
}
