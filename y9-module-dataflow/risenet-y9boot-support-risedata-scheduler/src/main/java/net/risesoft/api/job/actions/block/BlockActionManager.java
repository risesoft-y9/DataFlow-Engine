package net.risesoft.api.job.actions.block;

import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.JobAction;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description : 阻塞策略分发器
 * @ClassName BlockAction
 * @Author lb
 * @Date 2022/9/13 16:46
 * @Version 1.0
 */

/**
 * 阻塞任务分发器
 */
@Component
public class BlockActionManager implements JobAction {


    @Autowired
    Map<String, BlockJobAction> blockJobActionMap;

    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {
        BlockJobAction jobAction = blockJobActionMap.get(job.getBlockingStrategy());
        if (jobAction != null) {
            return jobAction.action(job, jobLog, taskExecutor, jobContext);
        }
        throw new JobException("未知的阻塞策略" + job.getBlockingStrategy());
    }


}
