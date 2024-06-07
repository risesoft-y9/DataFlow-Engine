package net.risesoft.api.persistence.dao.job;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.api.persistence.model.job.JobInfo;

import java.util.List;

/**
 * @Description :
 * @ClassName JobInfoDao
 * @Author lb
 * @Date 2022/9/20 17:00
 * @Version 1.0
 */
public interface JobInfoDao extends Repository {
    @Search("select count(SUCCESS)+count(ERROR) from Y9_DATASERVICE_JOB_INFO where environment=?")
    Long getCount(String environment);

    @Search("select * from Y9_DATASERVICE_JOB_INFO where environment=?1 and INFO_DATE >= ?2 and INFO_DATE <= ?3 ")
    List<JobInfo> search(String environment, String startTime, String endTime);

    @Modify("update  Y9_DATASERVICE_JOB_INFO  set success=success+1 where  INFO_DATE=? and environment=?")
    Integer addSuccess(String date, String environment);

    @Modify("update  Y9_DATASERVICE_JOB_INFO  set error=error+1 where INFO_DATE=? and environment = ?")
    Integer addError(String date, String environment);

    @Modify("insert into Y9_DATASERVICE_JOB_INFO (INFO_DATE,ERROR,SUCCESS,ENVIRONMENT)    values(?,0,0,?)")
    Integer create(String date, String environment);
}
