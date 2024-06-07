package net.risesoft.api.persistence.job;

import java.util.List;

import net.risesoft.api.persistence.model.job.JobInfo;

/**
 * @Description : 任务信息
 * @ClassName JobInfoService
 * @Author lb
 * @Date 2022/9/20 16:53
 * @Version 1.0
 */
public interface JobInfoService {

    //拿到总调度次数
    Long getCount(String environment);

    /**
     * 获取info
     * @param startTime
     * @param endTime
     * @return
     */
    List<JobInfo> getInfo(String environment,String startTime,String endTime);


    void addSuccess(String environment);

    void addError(String environment);
}
