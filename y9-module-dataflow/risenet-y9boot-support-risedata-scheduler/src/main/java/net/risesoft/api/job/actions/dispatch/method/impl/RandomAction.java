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
import java.util.Random;

/**
 * @Description : 随机
 * @ClassName RandomAction
 * @Author lb
 * @Date 2022/9/14 17:27
 * @Version 1.0
 */
@Component("随机")
public class RandomAction extends AbstractDispatchAction implements DispatchJobAction {
	
	private static final Random random = new Random();
	
    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {
        LResult lResult = new LResult();
        try {
            List<ServiceInstance> instances = getService(job);
            int index = random.nextInt(instances.size());
            Result result;
            try {
                result = executorActionManager.action(job, jobLog, jobContext.getArgs(), instances.get(index), 
                		jobContext, (count, ins) -> instances.get(random.nextInt(instances.size()))).onSuccess((res) -> {
                    res = ObjectUtils.nullOf(res, "NULL");
                    taskExecutor.successJob(job, jobLog, "调度成功:" + res, res.toString(), jobContext);
                    lResult.end(new Object[]{res});
                }).onError((e) -> {
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
