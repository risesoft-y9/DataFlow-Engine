package net.risesoft.api.job.actions.block;

import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.dispatch.method.DispatchActionManager;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;
import org.springframework.stereotype.Component;

/**
 * @Description : 并行阻塞操作:执行继续执行调度
 * @ClassName ParallelAction
 * @Author lb
 * @Date 2022/9/13 16:45
 * @Version 1.0
 */
@Component(value = "并行")
public class ParallelAction implements BlockJobAction {

    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext JobContext) {
        taskExecutor.appendLog(jobLog.getId(), "任务阻塞-策略并行:继续调度!");
        return taskExecutor.toAction(DispatchActionManager.class, job, jobLog, JobContext);
    }

}
