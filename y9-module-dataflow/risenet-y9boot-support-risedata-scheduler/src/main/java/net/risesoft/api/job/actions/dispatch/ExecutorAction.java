package net.risesoft.api.job.actions.dispatch;

import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.actions.dispatch.executor.DoBalance;
import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;

import org.springframework.cloud.client.ServiceInstance;

import java.util.Map;

/**
 * @Description : 调度执行器
 * @ClassName ExecutorAction
 * @Author lb
 * @Date 2022/9/14 17:41
 * @Version 1.0
 */
public interface ExecutorAction  {
    /**
     * 执行步骤
     * @param job 调用的任务
     * @param args 参数
     * @param iServiceInstance 调用的服务
     */
    Result action(Job job, JobLog jobLog, Map<String,Object> args, ServiceInstance iServiceInstance, JobContext jobContext, DoBalance doBalance);
}
