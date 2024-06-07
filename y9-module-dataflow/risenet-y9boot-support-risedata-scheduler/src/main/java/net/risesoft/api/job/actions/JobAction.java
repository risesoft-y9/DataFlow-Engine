package net.risesoft.api.job.actions;


import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;

/**
 * @Description : 任务步骤
 * @ClassName Action
 * @Author lb
 * @Date 2022/9/13 15:46
 * @Version 1.0
 */
public interface JobAction {
    /**
     * 执行步骤
     * @param job 任务
     * @param jobLog 日志
     */
    LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext);

}
