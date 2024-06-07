package net.risesoft.api.job.actions.dispatch.method;

import com.alibaba.fastjson.JSON;
import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.JobAction;
import net.risesoft.api.job.actions.dispatch.DispatchJobAction;
import net.risesoft.api.job.actions.dispatch.method.impl.AssignAction;
import net.risesoft.api.job.creator.JobArgsCreator;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description : 调度步骤管理器
 * @ClassName DispatchAction
 * @Author lb
 * @Date 2022/9/14 17:17
 * @Version 1.0
 */
@Component
public class DispatchActionManager implements JobAction {
	
    @Autowired
    Map<String, DispatchJobAction> dispatchJobActionMap;
    
    @Autowired
    AssignAction assignAction;
    
    @Autowired
    JobArgsCreator jobArgsCreator;
    
    public static final String DISPATCH_ARGS="DISPATCH_ARGS";
    public static final String INSTANCE_SIZE="$instanceSize";
    
    @Override
    public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {

        JobAction jobAction = dispatchJobActionMap.get(job.getDispatchMethod());
        if (!StringUtils.isEmpty(job.getDispatchArgs())) {
            jobContext.put(DispatchActionManager.DISPATCH_ARGS, JSON.parse(jobArgsCreator.creator(jobContext,job.getDispatchArgs())));
        }
        if (jobAction != null) {
            taskExecutor.appendLog(jobLog.getId(), "开始执行:" + job.getDispatchMethod() + "调度方式!");
            return jobAction.action(job, jobLog, taskExecutor, jobContext);
        }
        throw new JobException("未知的调度方式:" + job.getDispatchMethod());
    }

}
