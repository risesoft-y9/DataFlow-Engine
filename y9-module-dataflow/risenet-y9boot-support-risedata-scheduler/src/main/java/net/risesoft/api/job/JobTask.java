package net.risesoft.api.job;

import cn.hutool.core.date.DateUtil;
import net.risedata.jdbc.commons.utils.DateUtils;
import net.risesoft.api.exceptions.JobException;
import net.risesoft.api.persistence.model.job.Job;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description :
 * @ClassName CheckJobTask
 * @Author lb
 * @Date 2022/9/13 17:10
 * @Version 1.0
 */
public class JobTask implements Runnable, Trigger {

    private Job job;

    private TaskManager taskManager;

    private Map<String, Object> metaInfo;

    public JobTask() {

    }

    public JobTask(Job job, TaskManager taskManager) {
        this.job = job;
        this.taskManager = taskManager;
    }


    public synchronized void putInfo(String key, Object value) {
        if (metaInfo == null) {
            metaInfo = new HashMap<>();
        }
        metaInfo.put(key, value);
    }

    @Override
    public void run() {
        if (taskManager.hasTask(this.job.getId())) {
            taskManager.getTaskExecutor().startJob(this.job);
        } else {
            taskManager.removeJob(this.job.getId(), this);
        }
    }

   
    @Override
    public synchronized Date nextExecutionTime(TriggerContext triggerContext) {
        switch (job.getDispatchType()) {
            case "cron":
                CronTrigger cronTrigger = new CronTrigger(job.getSpeed());
                Date nextExecTime = cronTrigger.nextExecutionTime(triggerContext);
                return nextExecTime;
            case "固定速度":
                return new Date(System.currentTimeMillis() + (Integer.parseInt(job.getSpeed()) * 1000));
            default:
                throw new JobException("未知的调度速度类型" + job.getDispatchType());
        }

    }


    /**
     * 当任务改变时
     *
     * @param job
     */
    public synchronized void onChange(Job job) {
        if (this.job.getSpeed().equals(job.getSpeed())) {
            this.job = job;
        } else {
            this.job = job;
            taskManager.refreshJob(this);
        }
    }

    public Object getInfo(String key) {
        if (metaInfo == null) {
            return null;
        }
        return metaInfo.get(key);
    }
}
