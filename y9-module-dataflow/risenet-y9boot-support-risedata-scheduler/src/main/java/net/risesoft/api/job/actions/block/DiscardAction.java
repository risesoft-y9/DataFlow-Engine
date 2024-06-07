package net.risesoft.api.job.actions.block;

import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;
import org.springframework.stereotype.Component;

/**
 * @Description : 丢弃后续调度 直接结束调度
 * @ClassName DiscardAction
 * @Author lb
 * @Date 2022/9/13 16:45
 * @Version 1.0
 */
@Component(value = "丢弃后续调度")
public class DiscardAction implements BlockJobAction {

    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext JobContext) {
        taskExecutor.endJob(job, jobLog, JobLog.SUCCESS, "任务结束-任务阻塞丢弃后续调度", JobContext);
        return new LResult().end(new Object[]{});
    }

}
