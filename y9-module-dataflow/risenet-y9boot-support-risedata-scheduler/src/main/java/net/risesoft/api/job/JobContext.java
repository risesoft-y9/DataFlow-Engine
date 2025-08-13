package net.risesoft.api.job;

import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.persistence.model.job.Job;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description : 任务调度上下文用于上下文之间传递参数
 * @ClassName JobContext
 * @Author lb
 * @Date 2022/9/13 17:10
 * @Version 1.0
 */
public class JobContext {

	private Map<String, Object> args;

	boolean isChildrenJob;

	public JobContext(Map<String, Object> args) {
		this.args = args;
	}

	public JobContext put(String key, Object value) {
		args.put(key, value);
		return this;
	}

	public Object getRequiredValue(String key) {
		Object obj = getArgs().get(key);
		if (obj == null) {
			throw new JobException("key:" + key + " 不存在");
		}
		return obj;
	}

	public Map<String, Object> getArgs() {
		return args;
	}

	public boolean isChildrenJob() {
		return isChildrenJob;
	}

	public void setChildrenJob(boolean childrenJob) {
		isChildrenJob = childrenJob;
	}
}
