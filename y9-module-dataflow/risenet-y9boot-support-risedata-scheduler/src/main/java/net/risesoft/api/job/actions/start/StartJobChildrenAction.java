package net.risesoft.api.job.actions.start;

import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.actions.JobAction;
import net.risesoft.api.job.actions.dispatch.executor.Result;
import net.risesoft.api.persistence.job.JobService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.LResult;
import net.risesoft.api.utils.ResultUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Description : 启动子任务操作器
 * @ClassName StartJobAction
 * @Author lb
 * @Date 2022/9/13 16:06
 * @Version 1.0
 */
@Component
public class StartJobChildrenAction implements JobAction {

	@Autowired
	JobService jobService;

	@Override
	public LResult action(Job job, JobLog jobLog, TaskExecutorService taskExecutor, JobContext jobContext) {

		String[] childJobs = job.getChildJobs().split(",");
		Job tempJob = null;
		taskExecutor.appendLog(jobLog.getId(), "执行子任务:" + Arrays.toString(childJobs));
		jobContext.setChildrenJob(true);
		ArrayList<LResult> results = new ArrayList<>();
		AtomicBoolean isEndJobChild = new AtomicBoolean(true);
		for (int i = 0; i < childJobs.length; i++) {
			tempJob = jobService.findByJobId(Integer.parseInt(childJobs[i]));
			if (tempJob != null) {
				if (!StringUtils.isEmpty(tempJob.getChildJobs())) {
					isEndJobChild.set(false);
				}
				results.add(taskExecutor.toAction(StartJobAction.class, tempJob, jobLog, jobContext));
			}
		}

		return ResultUtils.batchResult(results.toArray(new LResult[results.size()])).onEnd(res -> {
			if (isEndJobChild.get()) {
				jobContext.setChildrenJob(false);
			}

		});
	}
}
