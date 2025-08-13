package net.risesoft.api.persistence.job;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.job.JobTask;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.security.ConcurrentSecurity;

import java.util.List;
import java.util.Map;

/**
 * @Description : 定时调度服务
 * @ClassName JobService
 * @Author lb
 * @Date 2022/8/30 15:58
 * @Version 1.0
 */
public interface JobService {
	/**
	 * 保存一个任务
	 *
	 * @param job
	 * @return
	 */
	boolean saveJob(Job job);

	/**
	 * 根据id获取任务
	 *
	 * @param jobId
	 * @return
	 */
	Job findByJobId(Integer jobId);

	/**
	 * 根据任务id 删除任务
	 *
	 * @param id
	 * @return
	 */
	boolean deleteByJobId(Integer id);

	/**
	 * 获取到需要监控的
	 *
	 * @param watchServer
	 * @param environment
	 * @return
	 */
	List<Job> findDispatchJob(String watchServer, String environment, String serviceId);

	/**
	 * 分页查询任务
	 * 
	 * @param job
	 * @param pageable
	 * @param jurisdiction
	 * @return
	 */
	LPage<Job> search(Job job, LPageable pageable, ConcurrentSecurity jurisdiction);

	/**
	 * 查找不存在的
	 *
	 * @param instanceId 当前实例id
	 * @param ids        key 集合
	 * @param jobTask
	 * @return
	 */
	List<Job> findMiss(String instanceId, Integer[] ids, Map<Integer, JobTask> jobTask);

	/**
	 * 修改状态
	 *
	 * @param id
	 * @param status
	 */
	void setStatus(Integer id, int status);

	/**
	 * 查找当前服务监控
	 *
	 * @param instanceId
	 * @param environment
	 * @param serviceId
	 * @return
	 */
	List<Job> findWatch(String instanceId, String environment, String serviceId);

	/**
	 * 修改监控
	 *
	 * @param instanceId     当前id
	 * @param watchServer    监控服务
	 * @param ovlWatchServer 旧的监控名
	 */
	boolean updateWatch(Integer instanceId, String watchServer, String ovlWatchServer);

	/**
	 * 清理掉不属于该服务调度的任务调度
	 *
	 * @param ids
	 */
	void updateNoWatch(Integer[] ids);

	/**
	 * 根据id 查找获取可用的job
	 *
	 * @param id
	 * @return
	 */
	Job findByJobIdAndUse(Integer id);

	/**
	 * 判断是否为我这个服务调度 判断1: 状态是否为1 2: 监控服务是否为当前服务
	 *
	 * @param id
	 * @return
	 */
	boolean hasTask(Integer id);

	/**
	 * 获取所有任务返回描述和id
	 *
	 * @param securityJurisdiction
	 * @return
	 */
	List<Map<String, Object>> searchJob(Job job, ConcurrentSecurity securityJurisdiction);

	/**
	 * 查询所有的任务名字
	 * 
	 * @param environment
	 * @param securityJurisdiction
	 * @return
	 */
	List<String> searchJobService(Job job, ConcurrentSecurity securityJurisdiction);

	/**
	 * 根据环境查询任务数量
	 * 
	 * @param name
	 * @param environment
	 * @return
	 */
	int searchCountByEnvironment(String environment);

	/**
	 * 删除正在等待的任务
	 *
	 * @param jobId
	 * @return
	 */
	boolean killAwaitJob(Integer jobId);

	/**
	 * 结束一个任务
	 * 
	 * @param jobId
	 * @param jobLogId
	 * @param result
	 * @return
	 */
	boolean endJob(Integer jobId, String jobLogId, String result, String msg, Integer status);

	/**
	 * 根据服务环境获取任务
	 * 
	 * @param serviceId
	 * @param environment
	 * @return
	 */
	List<Job> findJobsByServiceId(String serviceId, String environment);

	/**
	 * 根据args参数查询数据
	 * 
	 * @param args
	 * @return
	 */
	List<Job> searchJobByArgs(String args);

	/**
	 * 根据args获取数量
	 * 
	 * @param args
	 * @return
	 */
	int findCountJobByArgs(String args);

	/**
	 * 根据id获取参数
	 * @param id
	 * @return
	 */
	List<String> findArgsById(String id);

	/**
	 * 根据状态获取调度任务数
	 * @param environment
	 * @param status
	 * @return
	 */
	int getTaskCountByStatus(String environment, Integer status, List<String> jobType, boolean isAdmin);
	
	/**
	 * 根据相关字段信息获取实体
	 * @param args
	 * @param type
	 * @param environment
	 * @param serviceId
	 * @return
	 */
	Job findByArgsAndTypeAndEnvironmentAndServiceId(String args, String type, String environment, String serviceId);

	/**
	 * 根据环境和业务类别查询调度任务数
	 * @param environment
	 * @param jobType
	 * @return
	 */
	int countByEnvironmentAndJobType(String environment, List<String> jobType, boolean isAdmin);
}
