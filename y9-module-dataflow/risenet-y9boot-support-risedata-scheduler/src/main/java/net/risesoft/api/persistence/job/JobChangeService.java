package net.risesoft.api.persistence.job;


import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.risesoft.api.persistence.model.job.Job;

/**
 * @Description : 任务改变表
 * @ClassName JobChangeService
 * @Author lb
 * @Date 2022/9/14 11:49
 * @Version 1.0
 */
public interface JobChangeService {
    /**
     * 插入一条改变数据
     * @param jobId
     */
    void insertChange(Integer jobId);

    List<Integer> searchChangeJobs();

    void delete(Integer jobId);

    /**
     * 批量删除
     * @param ids
     */
    void onDelete(Collection<Integer> ids);
}
