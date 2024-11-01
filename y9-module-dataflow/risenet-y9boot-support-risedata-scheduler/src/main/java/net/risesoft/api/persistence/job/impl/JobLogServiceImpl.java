package net.risesoft.api.persistence.job.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Service;

import cn.hutool.core.date.DateUtil;
import net.risedata.jdbc.builder.OperationBuilder;
import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.factory.OperationBuilderFactory;
import net.risedata.jdbc.operation.impl.CustomOperation;
import net.risedata.jdbc.operation.impl.InOperation;
import net.risedata.jdbc.operation.impl.SectionOperation;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risedata.register.service.IServiceInstanceFactory;
import net.risedata.rpc.provide.config.Application;
import net.risesoft.api.job.TaskManager;
import net.risesoft.api.listener.ClientListener;
import net.risesoft.api.message.MessageService;
import net.risesoft.api.persistence.dao.job.JobLogDao;
import net.risesoft.api.persistence.job.JobInfoService;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.job.JobService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.AutoIdUtil;
import net.risesoft.api.utils.SqlUtils;
import net.risesoft.exceptions.ServiceOperationException;
import net.risesoft.security.ConcurrentSecurity;

/**
 * @Description :
 * @ClassName JobLogServiceImpl
 * @Author lb
 * @Date 2022/9/13 16:33
 * @Version 1.0
 */
@Service
public class JobLogServiceImpl extends AutomaticCrudService<JobLog, String> implements JobLogService {

	@Autowired
	JobLogDao jobLogDao;

	@Autowired
	JobInfoService jobInfoService;

	@Override
	public boolean isBlock(Integer jobId, int timeOut, String logId) {
		synchronized (jobId.toString().intern()) {
			if (timeOut > 0) {
				return jobLogDao.isBlock(jobId, System.currentTimeMillis() - timeOut * 1000, logId) > 0;
			} else {
				return jobLogDao.isBlock(jobId, logId) > 0;
			}
		}
	}

	@Override
	public boolean saveLog(JobLog jobLog) {
		if (StringUtils.isEmpty(jobLog.getId())) {
			jobLog.setId(AutoIdUtil.getRandomId26());
			jobLog.setDispatchTime(System.currentTimeMillis());
			insert(jobLog);
		} else {
			updateById(jobLog);
		}
		return true;
	}

	@Override
	public boolean appendLog(String id, String logContext) {
		try {
			boolean result = jobLogDao.appendLog(id,
					DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " : " + getMsg(logContext) + "\n") > 0;
			saveMsg(id, logContext, MAX_LOG);
			return result;
		} catch (Exception e) {
			Application.logger.error("job log error" + e.getMessage() + "log_Id:" + id);

			subLog(id);
			boolean result = jobLogDao.appendLog(id,
					DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " : " + getMsg(logContext) + "\n") > 0;
			saveMsg(id, logContext, MAX_LOG);
			return result;
		}
	}

	private void subLog(String id) {
		String content = jobLogDao.findConsoleById(id);
		if (content.length() < 500) {
			return;
		}
		jobLogDao.updateLog(id,
				content.substring(0, 500) + "......\n" + content.substring(content.length() - 500, content.length()));
	}

	@Override
	public boolean updateStatus(String logId, Integer status, Integer ovlStatus) {
		return jobLogDao.updateStatus(logId, status, ovlStatus) > 0;
	}

	@Override
	public boolean updateStatus(String logId, Integer status) {
		return jobLogDao.updateStatus(logId, status) > 0;
	}

//TODO 没有获取到等待的任务的时间
	@Override
	public JobLog pollJob(Integer jobId, int timeOut) {
		if (timeOut < 1) {
			return jobLogDao.pollJob(jobId);
		}
		return jobLogDao.pollJob(jobId, System.currentTimeMillis() - timeOut * 1000);
	}

	@Override
	public void endJob(String id, int success, String msg, String result, String environment) {

		try {

			jobLogDao.endJob(id, success, DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " : " + getMsg(msg),
					System.currentTimeMillis(), getMsg(result));
			saveMsg(id, msg, MAX_LOG);
		} catch (Exception e) {
			e.printStackTrace();
			subLog(id);
			jobLogDao.endJob(id, success,
					DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " : 保存任务结束日志时出错异常信息" + e.getMessage() + "对应消息:"
							+ getMsg(msg) + "....",
					System.currentTimeMillis(),
					"返回结果:" + result.substring(0, result.length() > 1500 ? 1500 : result.length()) + "....");
			saveMsg(id, msg, MAX_LOG);
		}
		addInfo(success, environment);
	}

	private void addInfo(int status, String environment) {
		switch (status) {
		case JobLog.SUCCESS:
			jobInfoService.addSuccess(environment);
			break;
		case JobLog.ERROR:
			jobInfoService.addError(environment);
			break;
		default:
			break;
		}
	}

	@Override
	public void appendSource(String id, String source, String msg) {

		jobLogDao.appendSource(id, source + "|",
				DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss") + " : " + getMsg(msg) + "\n");
		saveMsg(id, msg, MAX_LOG);
	}

	private static final int MAX_LOG = 1500;

	/**
	 * 追加日志 一次最高1500
	 *
	 * @param msg   日志信息
	 * @param start 开始长度
	 */
	private void saveMsg(String id, String msg, int start) {
		if (!StringUtils.isEmpty(msg) && msg.length() > start) {
			int end = 0;
			while (msg.length() > start) {
				end = start + MAX_LOG;
				if (end > msg.length()) {
					end = msg.length();
				}
				jobLogDao.appendLog(id, msg.substring(start, end));
				start = end;
			}
		}
	}

	@Override
	public JobLog findDownJob(Integer id, List<String> logs) {
		if (logs == null || logs.size() == 0) {
			return jobLogDao.findDownJob(id);
		}
		return jobLogDao.findDownJob(id, logs);
	}

	@Override
	public JobLog findById(String id) {
		return jobLogDao.findById(id);
	}

	@Override
	public LPage<Map<String, Object>> search(JobLog job, LPageable page, ConcurrentSecurity securityJurisdiction,
			String jobType, Integer[] jobIds) {
		OperationBuilder operationBuilder = OperationBuilderFactory.builder("id", new CustomOperation((where) -> {
			List<String> jobTypes = securityJurisdiction.getJobTypes();
			// 无查询的jobType
			if (StringUtils.isEmpty(jobType)) {
				if (jobTypes.isEmpty()) {
					return false;
				}
				where.append("JOB_ID in (select ID from Y9_DATASERVICE_JOB where SERVICE_JOB_TYPE in ");
				SqlUtils.appendIn(jobTypes, where);
				where.append(")");
				return true;
			} else {
				// 存在查询的jobType
				if (jobTypes.isEmpty() || jobTypes.indexOf(jobType) != -1) {
					where.append("JOB_ID in (select ID from Y9_DATASERVICE_JOB where SERVICE_JOB_TYPE=?)");
					where.add(jobType);
					return true;
				} else {
					throw new ServiceOperationException("no security");
				}
			}
		})).builder("childJobId", new CustomOperation((where) -> {
			boolean flag = false;
			// 任务名查询
			if (!StringUtils.isEmpty(job.getJobName())) {
				where.append("job_id in  (select id from Y9_DATASERVICE_JOB where job_name like ?)");
				where.add("%" + job.getJobName() + "%");
				flag = true;
			}
			return flag;
		}));
		if (jobIds != null && jobIds.length > 0) {
			// id 查询
			operationBuilder.builder("jobId", new InOperation(true, jobIds));
		}
		return this.getSearchExecutor().searchForPage(job, "ID,DISPATCH_SERVER,DISPATCH_TIME"
				+ ",END_TIME,JOB_ID,RESULT,STATUS,DISPATCH_SOURCE,(select job_name from Y9_DATASERVICE_JOB where id = job_id) JOB_NAME"
				+ ",(select name from Y9_DATASERVICE_BUSINESS z where"
				+ " z.id = (select service_job_type from Y9_DATASERVICE_JOB c where c.id = JobLog.job_id)) JOB_TYPE_NAME",
				page, operationBuilder);
	}

	@Override
	public String findConsoleById(String id) {
		return jobLogDao.findConsoleById(id);
	}

	@Override
	public void clearLog(Date time) {
		jobLogDao.clearLog(time.getTime());
	}

	@Autowired
	IServiceInstanceFactory iServiceInstanceFactory;
	/**
	 * 任务超时时间单位秒 默认1小时
	 */
	@Value("${beta.job.jobTimeOut:3600}")
	private Integer defaultTimeOut;
	@Autowired
	JobService jobService;
	@Autowired
	MessageService messageService;

	@Autowired
	TaskManager taskManager;

//这里没有做环境区分
	@Override
	public void clearTimeOutJob() {
		Date date = new Date();
		Set<Integer> ids = new HashSet<>();
		List<Integer> clearEdJobIds = jobLogDao.searchClearTimeOutJob(System.currentTimeMillis(),
				iServiceInstanceFactory.getIsntance().getInstanceId());
		if (jobLogDao.clearTimeOutJob(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss") + " : 任务超时结束任务! \n", date.getTime(),
				date.getTime(), iServiceInstanceFactory.getIsntance().getInstanceId()) == clearEdJobIds.size()) {
			ids.addAll(clearEdJobIds);
		}
		jobLogDao.clearTimeOutJobAndAwait(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss") + " : 任务超时结束任务! \n",
				date.getTime(), date.getTime(), iServiceInstanceFactory.getIsntance().getInstanceId());
		List<Integer> ids2 = jobLogDao.searchClearDefaultTimeOut(System.currentTimeMillis() - (defaultTimeOut * 1000),
				iServiceInstanceFactory.getIsntance().getInstanceId());
		if (jobLogDao.clearDefaultTimeOut(DateUtil.format(date, "yyyy-MM-dd HH:mm:ss") + " : 任务超过默认时间结束任务! \n",
				date.getTime() - (defaultTimeOut * 1000), date,
				iServiceInstanceFactory.getIsntance().getInstanceId()) == ids2.size()) {
			ids.addAll(ids2);
		}
		Job tmp;
		for (Integer jobid : ids) {
			System.out.println("clear---" + jobid);
			tmp = jobService.findByJobId(jobid);
			if (tmp != null) {
				// TODO 如果任务已经被调用endJob了则是否还要继续调用endJob
				messageService.onJobError(tmp);
				taskManager.endJob(tmp, jobid);
			}
		}
	}

	@Override
	public Integer getLogCount(String environment) {
		return jobLogDao.getLogCount(environment);
	}

	@Override
	public List<JobLog> findDownJobs(Integer id, List<String> logs) {
		if (logs == null || logs.size() == 0) {
			return jobLogDao.findDownJobs(id);
		}
		return jobLogDao.findDownJobs(id, logs);
	}

	@Override
	public ServiceInstance getRunableMinInstance(List<ServiceInstance> instanceIds) {
		if (instanceIds.size() == 1) {
			return instanceIds.get(0);
		}
		StringBuilder sql = new StringBuilder();
		List<String> index = new ArrayList<>();
		boolean isAppend = false;
		for (int i = 0; i < instanceIds.size(); i++) {
			if (ClientListener.getConnection(instanceIds.get(i).getInstanceId()) != null) {
				if (isAppend) {
					sql.append(",");
				}
				sql.append(
						"(select count(*) $indexKey,max(DISPATCH_TIME) $TIME from Y9_DATASERVICE_JOB_log where status=0 and  dispatch_source like '%$instanceId|') as subquery"
								.replace("$indexKey", "INDEX_" + i).replace("$TIME", "TIME_INDEX_" + i)
								.replace("$instanceId", instanceIds.get(i).getInstanceId()));

				isAppend = true;
				index.add("INDEX_" + i);
			}
		}
		Map<String, Object> sizeMap = jobLogDao.findRunableMinInstance(sql.toString());

		int min = 9999;
		int minIndex = 0;
		int tempSize = 0;
		long tempTime = 0;
		long minTime = 0;
		for (int i = 0; i < index.size(); i++) {
			Long querSize = (Long) sizeMap.get(index.get(i));
			tempSize = querSize.intValue();
			tempTime = sizeMap.get("TIME_" + index.get(i)) == null ? Long.MAX_VALUE
					: ((BigDecimal) sizeMap.get("TIME_" + index.get(i))).longValue();
			if (min > tempSize || (min == tempSize && minTime > tempTime)) {
				minIndex = i;
				min = tempSize;
				minTime = tempTime;
			}
		}
		return instanceIds.get(minIndex);
	}

	@Override
	public boolean killAwaitJob(Integer jobId) {
		return jobLogDao.deleteAwaitJobLog(jobId) > 0;
	}

	@Override
	public LPage<Map<String, Object>> searchByGroup(Date startDate, Date endDate, String environment, LPageable page,
			String jobName) {
		JobLog jobLog = new JobLog();
		jobLog.setStatus(JobLog.ERROR);
		jobLog.setEnvironment(environment);

		return getSearchExecutor().searchForPage(jobLog,
				"(select job_name from Y9_DATASERVICE_JOB as a2 where a2.id = JobLog.job_id) job_name,job_id,max(dispatch_time) dispatch_time,count(*) count",
				page, getOperationBuilder(startDate, endDate, jobName), null, true);
	}

	private OperationBuilder getOperationBuilder(Date startDate, Date endDate, String jobName) {
		return OperationBuilderFactory.builder("dispatchTime",
				new SectionOperation(startDate.getTime(), endDate.getTime()), "id", new CustomOperation((where) -> {
					boolean flag = false;
					if (!StringUtils.isEmpty(jobName)) {
						where.append(" job_id in (select id from Y9_DATASERVICE_JOB where job_name like ?) ");
						where.add("%" + jobName + "%");
						flag = true;
					}
					where.append(" GROUP BY JOB_ID  ");
					return flag;
				}, 99));
	}

	public List<Map<String, Object>> searchByGroupLog(Date startDate, Date endDate, String environment,
			String jobName) {
		JobLog jobLog = new JobLog();
		jobLog.setStatus(JobLog.ERROR);
		jobLog.setEnvironment(environment);
		return getSearchExecutor().searchForList(jobLog,
				"(select job_name from Y9_DATASERVICE_JOB where id = JobLog.job_id) job_name,"
						+ "(select status from Y9_DATASERVICE_JOB_log where id= (select max(id) from Y9_DATASERVICE_JOB_log a where a.job_id=JobLog.job_id )) job_end_status,"
						+ "job_id,max(dispatch_time) dispatch_time,count(*) count,(select log_console from Y9_DATASERVICE_JOB_log "
						+ "where id= (select max(id) from Y9_DATASERVICE_JOB_log a where a.job_id=JobLog.job_id and a.status =2)  ) log_console",
				getOperationBuilder(startDate, endDate, jobName), null);

	}

	private String getMsg(String msg) {
		if (msg == null) {
			return "NULL";
		}
		if (msg.length() > 1500) {
			return msg.substring(0, 1500);
		}
		return msg;
	}

	@Override
	public Integer getExecutedCountByStatusAndTime(List<Integer> statusList, long start, long end) {
		// TODO Auto-generated method stub
		return jobLogDao.getExecutedCountByStatusAndTime(statusList, start, end);
	}

	@Override
	public List<Map<String, Object>> getExecutedCountGroupByDispatchTime(List<Integer> statusList, long start,
			long end) {
		return jobLogDao.getExecutedCountGroupByDispatchTime(statusList, start, end);
	}

	@Override
	public List<Map<String, Object>> getSchedulingInfo(List<Integer> statuslist, Long startTime, Long endTime,
			String environment) {
		return jobLogDao.getSchedulingInfo(statuslist, startTime, endTime, environment);
	}

	@Override
	public List<Map<String, Object>> getLogGroupInfo(List<Integer> statuslist, Long startTime, Long endTime) {
		return jobLogDao.getLogGroupInfo(statuslist, startTime, endTime);
	}

}
