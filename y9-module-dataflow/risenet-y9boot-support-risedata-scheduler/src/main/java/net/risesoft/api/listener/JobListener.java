package net.risesoft.api.listener;

import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;

/**
 * @Description :
 * @ClassName Job
 * @Author lb
 * @Date 2023/8/9 14:51
 * @Version 1.0
 */
public interface JobListener {



    void endJob(Job job, JobLog jobLog);
}
