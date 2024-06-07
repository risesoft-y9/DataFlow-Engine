package net.risesoft.api.job.actions.block;

import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;
import org.springframework.stereotype.Component;

/**
 * 丢弃后续调度
 *
 * @Description : 串行阻塞操作:将任务添加到任务管理器
 * @ClassName SerialAction
 * @Author lb
 * @Date 2022/9/13 16:45
 * @Version 1.0
 */
@Component(value = "串行")
public class SerialAction implements BlockJobAction {

    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext JobContext) {
        taskExecutor.appendLog(jobLog.getId(), "任务阻塞添加到任务队列中:");
        taskExecutor.getTaskManager().pushTask(job, jobLog);
        return new LResult().end(new Object[]{});
    }

}
