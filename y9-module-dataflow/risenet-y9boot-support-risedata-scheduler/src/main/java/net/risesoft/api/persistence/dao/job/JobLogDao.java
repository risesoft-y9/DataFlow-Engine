package net.risesoft.api.persistence.dao.job;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.api.persistence.model.job.JobLog;

public interface JobLogDao extends Repository {

    /**
     * 获取当前在执行的任务数
     *
     * @param jobId   任务id
     * @param outTime 超时时间
     * @return
     */
    @Search("select count(1) from  Y9_DATASERVICE_JOB_LOG  where JOB_ID = ? and status=0 and DISPATCH_TIME > ? and ID != ?")
    Integer isBlock(Integer jobId, Long outTime, String logId);

    /**
     * 获取当前在执行的任务数
     *
     * @param jobId 任务id
     * @return
     */
    @Search("select count(1) from  Y9_DATASERVICE_JOB_LOG  where JOB_ID = ? and status=0  and ID != ?")
    Integer isBlock(Integer jobId, String logId);

    /**
     * 添加日志
     *
     * @param id         日志id
     * @param logContext 添加的内容
     * @return
     */
    @Modify("update Y9_DATASERVICE_JOB_LOG set LOG_CONSOLE =  concat(LOG_CONSOLE,?2) where ID = ?1")
    Integer appendLog(String id, String logContext);

    /**
     * 修改状态
     *
     * @param logId     日志id
     * @param status    状态
     * @param ovlStatus 之前的状态
     * @return
     */
    @Modify("update Y9_DATASERVICE_JOB_LOG  set STATUS =?2 where ID=?1 and  STATUS =?3")
    Integer updateStatus(String logId, Integer status, Integer ovlStatus);

    /**
     * 修改状态
     *
     * @param logId  日志id
     * @param status 状态
     * @return
     */
    @Modify("update Y9_DATASERVICE_JOB_LOG set STATUS =?2 where ID=?1 ")
    Integer updateStatus(String logId, Integer status);


    /**
     * 获取最早的任务
     *
     * @param jobId
     * @return
     */
    @Search("select * from  Y9_DATASERVICE_JOB_LOG where job_ID = ?1 and status=" + JobLog.AWAIT + " and DISPATCH_TIME =" +
            " (select min(DISPATCH_TIME) from Y9_DATASERVICE_JOB_LOG  where job_ID = ?1 and status = " + JobLog.AWAIT + " )  ")
    JobLog pollJob(Integer jobId);


    /**
     * 获取一段时间内有效的任务
     *
     * @param jobId
     * @param time
     * @return
     */
    @Search("select * from  Y9_DATASERVICE_JOB_LOG where job_ID = ?1  and status=" + JobLog.AWAIT + " and DISPATCH_TIME =" +
            " (select min(DISPATCH_TIME) from Y9_DATASERVICE_JOB_LOG  where job_ID = ?1 and status = " + JobLog.AWAIT + " and DISPATCH_TIME >?2 )  ")
    JobLog pollJob(Integer jobId, Long time);

    @Modify("update Y9_DATASERVICE_JOB_LOG set STATUS =?2 ,LOG_CONSOLE =  concat(LOG_CONSOLE,?3),END_TIME=?4,RESULT=?5  where ID=?1 and status in(-1,0)")
    void endJob(String id, Integer success, String s, Long date, String toJSONString);

    @Modify("update Y9_DATASERVICE_JOB_LOG set DISPATCH_SOURCE =  concat(DISPATCH_SOURCE,?2) ,LOG_CONSOLE =  concat(LOG_CONSOLE,?3) where ID=?1 ")
    void appendSource(String id, String source, String s);

    @Search("select * from Y9_DATASERVICE_JOB_LOG where status=0 and job_ID=?1 and id not in (?2) and DISPATCH_TIME=(select min(DISPATCH_TIME) from Y9_DATASERVICE_JOB_LOG where status=0 and job_ID=?1 and id not in (?2))")
    JobLog findDownJob(Integer id, List<String> logs);

    @Search("select * from Y9_DATASERVICE_JOB_LOG where status=0 and job_ID=?1  and DISPATCH_TIME=(select min(DISPATCH_TIME) from Y9_DATASERVICE_JOB_LOG where status=0 and job_ID=?1)  ")
    JobLog findDownJob(Integer id);

    @Search("select * from Y9_DATASERVICE_JOB_LOG where id=?")
    JobLog findById(String id);

    @Search("select LOG_CONSOLE from Y9_DATASERVICE_JOB_LOG where id=?")
    String findConsoleById(String id);

    @Modify("delete from Y9_DATASERVICE_JOB_LOG where DISPATCH_TIME < ?")
    Integer clearLog(Long time);

    @Modify("update Y9_DATASERVICE_JOB_LOG set LOG_CONSOLE =  concat(LOG_CONSOLE,?1),RESULT=?1,status=2,end_time=?3 where status =0" +
            " and JOB_ID in (select ID from Y9_DATASERVICE_JOB where TIME_OUT>0 and dispatch_server=?4)" +
            " and dispatch_time < ?2-(select TIME_OUT*1000 from Y9_DATASERVICE_JOB where ID=JOB_ID)")
    Integer clearTimeOutJob(String msg, Long newTime, Long endTime,String serviceId);

//TODO 有问题 会出现结束的任务还查询到的情况 加上服务监控做条件只能操作自己所管理的服务
    @Search("select JOB_ID from  Y9_DATASERVICE_JOB_LOG  where status =0 and environment=?2  " +
            "and JOB_ID in (select ID from Y9_DATASERVICE_JOB where TIME_OUT>0 and dispatch_server=?2 )" +
            "and dispatch_time < ?1-(select TIME_OUT*1000 from Y9_DATASERVICE_JOB where ID=JOB_ID)")
    List<Integer> searchClearTimeOutJob(Long newTime,String serviceId);

    @Modify("update Y9_DATASERVICE_JOB_LOG set LOG_CONSOLE =  concat(LOG_CONSOLE,?1),RESULT=?1,status=2,end_time=?3 where status =-1 " +
            "and JOB_ID in (select ID from Y9_DATASERVICE_JOB where TIME_OUT>0 and dispatch_server=?4)" +
            "and dispatch_time < ?2-(select TIME_OUT*1000 from Y9_DATASERVICE_JOB where ID=JOB_ID)")
    Integer clearTimeOutJobAndAwait(String msg, Long newTime, Long endTime,String serviceId);

    @Search("select count(*) from Y9_DATASERVICE_JOB_LOG where environment=?")
    Integer getLogCount(String environment);

    @Search("select * from Y9_DATASERVICE_JOB_LOG where status=0 and job_ID=?1")
    List<JobLog> findDownJobs(Integer id);

    @Search("select * from Y9_DATASERVICE_JOB_LOG where status=0 and job_ID=?1 and id not in (?2)")
    List<JobLog> findDownJobs(Integer id, List<String> logs);

    @Modify("update Y9_DATASERVICE_JOB_LOG set LOG_CONSOLE =  concat(LOG_CONSOLE,?1),RESULT=?1,status=2,end_time=?3 where status=0 " +
            "and JOB_ID in (select ID from Y9_DATASERVICE_JOB where TIME_OUT = 0 and dispatch_server=?4)" +
            "and dispatch_time < ?2")
    Integer clearDefaultTimeOut(String s, Long l, Date date,String serviceId);

    @Search("select JOB_ID from  Y9_DATASERVICE_JOB_LOG  where status=0 " +
            "and JOB_ID in (select ID from Y9_DATASERVICE_JOB where TIME_OUT = 0 and dispatch_server=?2)" +
            "and dispatch_time < ?1")
    List<Integer> searchClearDefaultTimeOut(Long l,String serviceId);

    @Search("select * from #{?1}")
    Map<String, Object> findRunableMinInstance(String sql);

    @Modify("update Y9_DATASERVICE_JOB_LOG set LOG_CONSOLE = ?2 where ID=?1 ")
    void updateLog(String id, String s);

    @Modify("delete from Y9_DATASERVICE_JOB_LOG where job_id=? and status = " + JobLog.AWAIT)
    int deleteAwaitJobLog(Integer jobId);
    
    @Modify("delete from Y9_DATASERVICE_JOB_LOG where job_id=?1")
    int deleteJobLog(Integer jobId);
    
    @Search("select count(*) from Y9_DATASERVICE_JOB_LOG "
		+ "where STATUS in (?1) and ENVIRONMENT = ?2 and JOB_ID in (select ID from Y9_DATASERVICE_JOB where SERVICE_JOB_TYPE in (?3))")
    Integer getCount(List<Integer> status, String environment, List<String> jobTypes);
    
    @Search("select count(*) from Y9_DATASERVICE_JOB_LOG where STATUS in (?1) and ENVIRONMENT = ?2")
    Integer getCount(List<Integer> status, String environment);
    
    @Search("select count(*) from Y9_DATASERVICE_JOB_LOG where ENVIRONMENT = ?1 and DISPATCH_TIME >= ?2 and DISPATCH_TIME <= ?3")
    Integer getCount(String environment, Long startTime, Long endTime);
    
    @Search("select count(*) from Y9_DATASERVICE_JOB_LOG "
		+ "where ENVIRONMENT = ?1 and DISPATCH_TIME >= ?2 and DISPATCH_TIME <= ?3 "
		+ "and JOB_ID in (select ID from Y9_DATASERVICE_JOB where SERVICE_JOB_TYPE in (?4))")
    Integer getCount(String environment, Long startTime, Long endTime, List<String> jobTypes);

    @Search("select count(*) from Y9_DATASERVICE_JOB_LOG "
		+ "where ENVIRONMENT = ?1 and DISPATCH_TIME >= ?2 and DISPATCH_TIME <= ?3 and STATUS in (?4)")
    Integer getJobCount(String environment, Long startTime, Long endTime, List<Integer> status);

    @Search("select count(*) from Y9_DATASERVICE_JOB_LOG "
		+ "where ENVIRONMENT = ?1 and DISPATCH_TIME >= ?2 and DISPATCH_TIME <= ?3 and STATUS in (?5) "
		+ "and JOB_ID in (select ID from Y9_DATASERVICE_JOB where SERVICE_JOB_TYPE in (?4))")
    Integer getJobCount(String environment, Long startTime, Long endTime, List<String> jobTypes, List<Integer> status);
    
}
