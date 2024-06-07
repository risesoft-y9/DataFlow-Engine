package net.risesoft.api.job.actions.dispatch.method.impl;

import com.alibaba.fastjson.JSON;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.dispatch.DispatchJobAction;
import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.job.actions.dispatch.method.AbstractDispatchAction;
import net.risesoft.api.job.actions.dispatch.method.DispatchActionManager;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;
import net.risesoft.api.utils.ResultUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description : 分片广播方式
 * @ClassName BroadcastAction
 * @Author lb
 * @Date 2022/9/14 17:27
 * @Version 1.0
 */
@Component("分片广播")
public class BroadcastAction extends AbstractDispatchAction implements DispatchJobAction {


    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {
        Map<String, Object> res = new HashMap<>();
        try {
            List<ServiceInstance> instances = getService(job);

            Result[] results = new Result[instances.size()];
            jobContext.put(DispatchActionManager.INSTANCE_SIZE, instances.size());
            for (int i = 0; i < instances.size(); i++) {
                try {
                    ServiceInstance instance = instances.get(i);
                    jobContext.put("index", i);
                    results[i] = onResult(jobLog.getId(), res, taskExecutor, instance, executorActionManager.action(job, jobLog, jobContext.getArgs(), instance,jobContext,null));
                } catch (Exception e) {
                    taskExecutor.appendLog(jobLog.getId(), instances.get(i).getInstanceId() + "调度失败原因:" + e.getMessage());

                }
            }
            return ResultUtils.batchResult(results).onEnd((ress) -> {
                taskExecutor.successJob(job, jobLog, "调度结束", "广播调度成功!返回值" + res, jobContext);
            });
        } catch (Exception e) {
            taskExecutor.endJob(job, jobLog, JobLog.ERROR, e.getMessage(), jobContext);
            return new LResult().end(new Object[]{e});
        }
    }


}
