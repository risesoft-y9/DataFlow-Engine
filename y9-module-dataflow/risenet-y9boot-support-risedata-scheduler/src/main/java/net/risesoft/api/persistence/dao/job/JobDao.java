package net.risesoft.api.persistence.dao.job;

import java.util.List;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.repository.Repository;
import net.risesoft.api.persistence.model.job.Job;

/**
 * @Description :
 * @ClassName IServiceDao
 * @Author lb
 * @Date 2022/8/3 16:04
 * @Version 1.0
 */
public interface JobDao extends Repository {

	/**
	 * 根据id 查找
	 *
	 * @param id
	 * @return
	 */
	@Search("select * from Y9_DATASERVICE_JOB where ID = ?")
	Job findById(Integer id);

	/**
	 * 修改状态
	 *
	 * @param id     id
	 * @param status 状态
	 * @return
	 */
	@Modify("update Y9_DATASERVICE_JOB set STATUS = ?2 where ID = ?1")
	Integer updateStatus(Integer id, Integer status);

	/**
	 * 查找 排除 获取没有设置监控服务的 获取监控服务中
	 *
	 * @param watchServer 当前监控id
	 * @return
	 */
	@Search("select * from Y9_DATASERVICE_JOB t WHERE (DISPATCH_SERVER != ?1 or DISPATCH_SERVER is null) and status=1 and (ENVIRONMENT = ?2 or (SELECT count(*) "
			+ "FROM Y9_DATASERVICE_ISERVICE t2 where SERVICE_Id=?3 and  t2.ENVIRONMENT=t.ENVIRONMENT)=0)  and (DISPATCH_SERVER is null or "
			+ "DISPATCH_SERVER = '' or DISPATCH_SERVER NOT IN (SELECT INSTANCE_Id FROM Y9_DATASERVICE_ISERVICE where SERVICE_Id=?3))")
	List<Job> findWatch(String watchServer, String environment, String serviceId);

	/**
	 * 抢占监控
	 *
	 * @param instanceId     任务id
	 * @param watchServer    之前的服务
	 * @param ovlWatchServer 以前的监控服务
	 * @return
	 */
	@Modify("update Y9_DATASERVICE_JOB set DISPATCH_SERVER = ?2 where ID = ?1 and (DISPATCH_SERVER=?3 or DISPATCH_SERVER is null)")
	Integer updateWatch(Integer instanceId, String watchServer, String ovlWatchServer);

	/**
	 * 拿到 不存在的
	 *
	 * @param instanceId
	 * @param ids
	 * @return
	 */
	@Search("select * from Y9_DATASERVICE_JOB  where DISPATCH_SERVER=?1 and ID not in (?2) and status=1 ")
	List<Job> findMiss(String instanceId, Integer[] ids);

	@Search("select id from Y9_DATASERVICE_JOB  where DISPATCH_SERVER=?1 ")
	List<Integer> findJobIds(String instanceId);

	@Search("select MAX(ID) from Y9_DATASERVICE_JOB")
	Integer getMaxId();

	/**
	 * 把不属于此服务调度的取消掉
	 *
	 * @param instanceId
	 * @param ids
	 * @param serviceId
	 * @param environment
	 */
	@Modify("update Y9_DATASERVICE_JOB t set DISPATCH_SERVER='' where DISPATCH_SERVER=?1 and ID  in (?2) "
			+ " and ENVIRONMENT != ?4 and ((select count(1) from Y9_DATASERVICE_ISERVICE t2 where t2.service_Id=?3 and t2.ENVIRONMENT=t.ENVIRONMENT and status=0)>0)")
	void updateNoWatch(String instanceId, Integer[] ids, String serviceId, String environment);

	@Search("select * from Y9_DATASERVICE_JOB  where DISPATCH_SERVER=?  and status=1 ")
	List<Job> findMiss(String instanceId);

	@Search("select * from Y9_DATASERVICE_JOB where ID = ?2 and status = 1 and DISPATCH_SERVER=?1")
	Job findByJobIdAndUse(String serverId, Integer id);

	@Search("select count(*) from Y9_DATASERVICE_JOB where ID = ?2 and status = 1 and DISPATCH_SERVER=?1")
	Integer hasTask(String instanceId, Integer id);

	@Search("select count(*) from Y9_DATASERVICE_JOB where environment=?")
	Integer getJobCount(String environment);

	@Search("select count(distinct service_Id) from Y9_DATASERVICE_JOB  where environment=?")
	Integer getServiceCount(String environment);

	@Search("select count(*) from Y9_DATASERVICE_JOB where SERVICE_JOB_TYPE=? and environment=?")
	int searchCountByJobType(String name, String environment);

	@Search("select * from Y9_DATASERVICE_JOB where Service_id=? and environment=? and status = 1")
	List<Job> findJobsByServiceId(String serviceId, String environment);

	@Search("select count(*) from Y9_DATASERVICE_JOB where instr(args,?)>0 ")
	int findCountJobByArgs(String args);

	@Search("select * from Y9_DATASERVICE_JOB where instr(args,?)>0 ")
	List<Job> searchJobByArgs(String args);

	@Search("select args from Y9_DATASERVICE_JOB where id=? ")
	String findArgsById(String id);
	
	@Search("select count(*) from Y9_DATASERVICE_JOB where STATUS in(?1)")
	Integer getallJobCountByStatus(List<Integer> JobStatus);

	@Search("SELECT COUNT(*) FROM Y9_DATASERVICE_JOB  WHERE id IN (select Y9_DATASERVICE_JOB_LOG.JOB_ID FROM Y9_DATASERVICE_JOB_LOG where Y9_DATASERVICE_JOB_LOG.status in (?1) and Y9_DATASERVICE_JOB_LOG.DISPATCH_TIME >=?2  and Y9_DATASERVICE_JOB_LOG.DISPATCH_TIME<=?3) and  Y9_DATASERVICE_JOB.STATUS in(?4)")
	Integer getActiveTaskCountByTime(List<Integer> logStatus, Long startTime, Long endTime, List<Integer> jobStatus);
	
	
}
