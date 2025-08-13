package net.risesoft.api.persistence.job;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.client.ServiceInstance;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.security.ConcurrentSecurity;

/**
 * @Description : 调度日志服务
 * @ClassName JobLogService
 * @Author lb
 * @Date 2022/9/13 16:13
 * @Version 1.0
 */
public interface JobLogService {
	/**
	 * 是否阻塞
	 * 
	 * @param jobId   任务id
	 * @param timeOut 超时时间
	 * @param logId   当前日志id
	 * @return
	 */
	boolean isBlock(Integer jobId, int timeOut, String logId);

	/**
	 * 保存日志
	 * 
	 * @param jobLog
	 * @return
	 */
	boolean saveLog(JobLog jobLog);

	/**
	 * 添加日志
	 * 
	 * @param id         日志id
	 * @param logContext 添加的内容
	 * @return
	 */
	boolean appendLog(String id, String logContext);

	/**
	 * 修改状态
	 * 
	 * @param logId     日志id
	 * @param status    状态
	 * @param ovlStatus 之前的状态
	 * @return
	 */
	boolean updateStatus(String logId, Integer status, Integer ovlStatus);

	/**
	 * 修改状态
	 * 
	 * @param logId  日志id
	 * @param status 状态
	 * @return
	 */
	boolean updateStatus(String logId, Integer status);

	/**
	 * 获取最早在排队的任务进行调度
	 * 
	 * @param jobId
	 * @return
	 */
	JobLog pollJob(Integer jobId, int timeOut);

	/**
	 * 结束任务
	 * 
	 * @param id      id
	 * @param success 状态
	 * @param msg     消息
	 * @param result  返回值
	 */
	void endJob(String id, int success, String msg, String result, String environment);

	/**
	 * 添加调度源
	 * 
	 * @param id
	 * @param source
	 * @param msg
	 */
	void appendSource(String id, String source, String msg);

	/**
	 * 获取到因为掉线没执行的job
	 * 
	 * @param id
	 * @param logs
	 * @return
	 */
	JobLog findDownJob(Integer id, List<String> logs);

	/**
	 * 根据id 查找
	 * 
	 * @param id
	 * @return
	 */
	JobLog findById(String id);

	/**
	 * 查询日志
	 * 
	 * @param job
	 * @param page
	 * @param securityJurisdiction
	 * @param service
	 * @param jobIds
	 * @return
	 */
	LPage<Map<String, Object>> search(JobLog job, LPageable page, ConcurrentSecurity securityJurisdiction,
			String jobType, Integer[] jobIds);

	String findConsoleById(String id);

	/**
	 * 清除日志 小于这个时间的
	 * 
	 * @param time
	 */
	void clearLog(Date time);

	/**
	 * 清理掉调度超时的任务
	 */
	void clearTimeOutJob();

	/**
	 * 获取执行任务的数量
	 * 
	 * @param environment
	 * @return
	 */
	Integer getLogCount(String environment);

	/**
	 * 拿到当前job在运行中的
	 * 
	 * @param id
	 * @param logs
	 * @return
	 */
	List<JobLog> findDownJobs(Integer id, List<String> logs);

	/**
	 * 拿到运行任务最少的
	 * 
	 * @param instanceIds
	 * @return
	 */
	ServiceInstance getRunableMinInstance(List<ServiceInstance> instanceIds);

	/**
	 * 删除掉正在等待的任务
	 * 
	 * @param jobId
	 * @return
	 */
	boolean killAwaitJob(Integer jobId);

	LPage<Map<String, Object>> searchByGroup(Date startDate, Date endDate, String environment, LPageable page,
			String jobName, ConcurrentSecurity jurisdiction);

	List<Map<String, Object>> searchByGroupLog(Date startDate, Date endDate, String environment, String jobName);

	/**
	 * 根据条件获取数量
	 * @param status
	 * @param environment
	 * @param jobTypes
	 * @return
	 */
	int getCount(List<Integer> status, String environment, List<String> jobTypes, boolean isAdmin);

	/**
	 * 根据时间区间获取每日的调度次数
	 * @param startTime
	 * @param endTime
	 * @param environment
	 * @param jobTypes
	 * @param isAdmin
	 * @return
	 */
	Integer getFrequencyCount(Long startTime, Long endTime, String environment, List<String> jobTypes, boolean isAdmin);

	/**
	 * 根据状态获取每日调度情况
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @param environment
	 * @param jobTypes
	 * @param isAdmin
	 * @return
	 */
	Integer getJobCount(List<Integer> status, Long startTime, Long endTime, String environment, List<String> jobTypes, boolean isAdmin);
}
