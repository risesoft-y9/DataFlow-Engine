package net.risesoft.api.job.actions.dispatch.method.impl;

import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
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
 * @Description : 轮询
 * @ClassName PolltAction
 * @Author lb
 * @Date 2022/9/14 17:27
 * @Version 1.0
 */
@Component("轮询")
public class PolltAction extends AbstractDispatchAction {


    private static final String KEY = "POLL_INDEX";

    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {
        LResult lResult = new LResult();
        try {
            List<ServiceInstance> instances = getService(job);
            Integer index = taskExecutor.getTaskManager().getInfo(job.getId(), KEY, Integer.class);
            if (index == null || index >= instances.size()) {
                index = 0;
            }
            final int i = index;
            executorActionManager.action(job, jobLog, jobContext.getArgs(), instances.get(i),jobContext,(count,ins)->{
                taskExecutor.getTaskManager().putInfo(job.getId(), KEY, i + 1);
                return instances.get(taskExecutor.getTaskManager().getInfo(job.getId(),KEY,Integer.class));
            }).onSuccess((res) -> {
                res = ObjectUtils.nullOf(res, "NULL");
                taskExecutor.successJob(job, jobLog, "调度成功:" + res, res.toString(), jobContext);
                lResult.end(new Object[]{res});
                taskExecutor.getTaskManager().putInfo(job.getId(), KEY, i + 1);
            }).onError((e) -> {
                taskExecutor.endJob(job, jobLog, JobLog.ERROR, e.getMessage(), jobContext);
                taskExecutor.getTaskManager().putInfo(job.getId(), KEY, i + 1);
                lResult.end(new Object[]{e});
            });
        } catch (Exception e) {
            taskExecutor.endJob(job, jobLog, JobLog.ERROR, e.getMessage(), jobContext);
            lResult.end(new Object[]{e});
        }
        return lResult;
    }
}
