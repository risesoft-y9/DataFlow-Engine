package net.risesoft.api.job.actions.start;

import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.JobAction;
import net.risesoft.api.job.actions.block.BlockActionManager;
import net.risesoft.api.job.actions.dispatch.method.DispatchActionManager;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;
import org.springframework.stereotype.Component;

/**
 * @Description : 启动任务操作器
 * @ClassName StartJobAction
 * @Author lb
 * @Date 2022/9/13 16:06
 * @Version 1.0
 */
@Component
public class StartJobAction implements JobAction {

    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {
        jobContext.put("jobId", job.getId());
        jobContext.put("jobLogId", jobLog.getId());
        if (taskExecutor.getTaskManager().isBlock(job, jobLog.getId())) {
            taskExecutor.appendLog(jobLog.getId(), "任务阻塞 ");
            return taskExecutor.toAction(BlockActionManager.class, job, jobLog, jobContext);
        } else {
            return taskExecutor.toAction(DispatchActionManager.class, job, jobLog, jobContext);
        }
    }
}
