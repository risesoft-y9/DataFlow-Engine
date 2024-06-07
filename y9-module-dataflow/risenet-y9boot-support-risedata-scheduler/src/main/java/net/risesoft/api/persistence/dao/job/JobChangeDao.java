package net.risesoft.api.persistence.dao.job;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.api.persistence.model.job.Job;

import java.util.Collection;
import java.util.List;

/**
 * @Description : 任务改变表
 * @ClassName IServiceDao
 * @Author lb
 * @Date 2022/8/3 16:04
 * @Version 1.0
 */
public interface JobChangeDao extends Repository {

    /**
     * 根据id 查找
     *
     * @param id
     * @return
     */
    @Search("select count(*) from Y9_DATASERVICE_JOB_CHANGE where ID = ?")
    Integer findById(Integer id);

    @Modify("insert into Y9_DATASERVICE_JOB_CHANGE values(?)")
    Integer insert(Integer id);

    @Modify("delete from  Y9_DATASERVICE_JOB_CHANGE where ID=?")
    void delete(Integer jobId);

    @Search(" select ID from Y9_DATASERVICE_JOB_CHANGE ")
    List<Integer> searchChangeJobs();

    @Modify("delete from  Y9_DATASERVICE_JOB_CHANGE where ID in (?1)")
    void batchDelete(Collection<Integer> ids);
}
