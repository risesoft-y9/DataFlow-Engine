package net.risesoft.api.persistence.job.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risedata.jdbc.builder.OperationBuilder;
import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.factory.OperationBuilderFactory;
import net.risedata.jdbc.operation.impl.CustomOperation;
import net.risedata.jdbc.search.LPageable;
import net.risedata.jdbc.service.impl.AutomaticCrudService;
import net.risedata.register.service.IServiceInstanceFactory;
import net.risesoft.api.job.JobContext;
import net.risesoft.api.job.JobTask;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.job.TaskManager;
import net.risesoft.api.persistence.dao.job.JobDao;
import net.risesoft.api.persistence.dao.job.JobLogDao;
import net.risesoft.api.persistence.job.JobChangeService;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.job.JobService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.utils.SqlUtils;
import net.risesoft.security.ConcurrentSecurity;

/**
 * @Description :
 * @ClassName JobServiceImpl
 * @Author lb
 * @Date 2022/8/30 16:05
 * @Version 1.0
 */
@Service
public class JobServiceImpl extends AutomaticCrudService<Job, Integer> implements JobService {

	@Autowired
	JobDao jobDao;
	
	@Autowired
	JobLogDao jobLogDao;

	@Autowired
	IServiceInstanceFactory iServiceInstanceFactory;

	@Autowired(required = false)
	TaskManager taskManager;

	@Autowired
	JobChangeService jobChangeService;

	@Override
	@Transactional
	public boolean saveJob(Job job) {
		job.setUpdateTime(System.currentTimeMillis());
		if (job.getDispatchType().equals("cron")) {
			try {
				CronExpression.parse(job.getSpeed()).next(LocalDateTime.now());
			} catch (Exception e) {
				throw new RuntimeException("cron 表达式错误!");
			}
		}
		if (job.getId() == null || job.getId() == 0) {
			job.setDispatchServer("");
			job.setCreateDate(new Date());
			job.setId(getMaxId());
			if (job.getStatus() == 1) {
				job.setDispatchServer(iServiceInstanceFactory.getIsntance().getInstanceId());
			}

			insert(job);
			if (job.getStatus() == 1) {
				taskManager.addTask(job);
			}
			return true;
		} else {
			updateById(job);
			if (job.getStatus() == 1 && taskManager != null) {
				jobChangeService.insertChange(job.getId());
				taskManager.pushJobChange(job.getId());
			}
			return true;
		}
	}

	private Integer getMaxId() {
		Integer maxId = jobDao.getMaxId();
		return maxId == null ? 1 : maxId + 1;
	}

	@Override
	public Job findByJobId(Integer jobId) {
		return jobDao.findById(jobId);
	}

	@Override
	public boolean deleteByJobId(Integer id) {
		jobLogDao.deleteJobLog(id);
		return deleteById(id) > 0;
	}

	@Override
	public List<Job> findDispatchJob(String watchServer, String environment, String serviceId) {
		return jobDao.findWatch(watchServer, environment, serviceId);
	}

	@Override
	public LPage<Job> search(Job job, LPageable pageable, ConcurrentSecurity jurisdiction) {
		return searchForPage(job, pageable, null, createBuilder("serviceId", jurisdiction));
	}

	/**
	 * 获取权限的操作方法 根据任务类型区分权限 会在sql后面追加JOB_TYPE IN ????
	 * 
	 * @param field
	 * @param jurisdiction
	 * @return
	 */
	public static OperationBuilder createBuilder(String field, ConcurrentSecurity jurisdiction) {
		return OperationBuilderFactory.builder(field, new CustomOperation((where) -> {
			if (jurisdiction.getJobTypes().isEmpty()) {
				return false;
			}

			where.append("SERVICE_JOB_TYPE in  ");
			SqlUtils.appendIn(jurisdiction.getJobTypes(), where);
			return true;
		}));
	}

	@Autowired
	JdbcTemplate jt;

	@Override
	public List<Job> findMiss(String instanceId, Integer[] ids, Map<Integer, JobTask> jobTask) {

		if (ids.length == 0) {
			return jobDao.findMiss(instanceId);
		}
		if (ids.length > 999) {
			List<Integer> jobIds = jobDao.findJobIds(instanceId);
			List<Job> jobs = new ArrayList<>();
			for (Integer jobId : jobIds) {
				if (!jobTask.containsKey(jobId)) {
					jobs.add(jobDao.findById(jobId));
				}
			}
			return jobs;
		}
		return jobDao.findMiss(instanceId, ids);
	}

	@Override
	public void setStatus(Integer id, int status) {
		jobDao.updateStatus(id, status);
		if (status == 1 && taskManager != null) {
			jobChangeService.insertChange(id);
			taskManager.pushJobChange(id);
		}
	}

	@Override
	public List<Job> findWatch(String instanceId, String environment, String serviceId) {
		return jobDao.findWatch(instanceId, environment, serviceId);
	}

	@Override
	public boolean updateWatch(Integer id, String watchServer, String ovlInstanceId) {
		return jobDao.updateWatch(id, watchServer, ovlInstanceId) > 0;
	}

	@Override
	public void updateNoWatch(Integer[] ids) {
		if (ids == null || ids.length == 0) {
			return;
		}
		if (ids.length > 999) {
			int split = ids.length % 999 == 0 ? ids.length / 999 : ((ids.length / 999) + 1);
			for (int i = 0; i < split; i++) {
				jobDao.updateNoWatch(iServiceInstanceFactory.getIsntance().getInstanceId(),
						Arrays.copyOfRange(ids, i * 999, i == split - 1 ? ids.length : (i + 1) * 999),
						iServiceInstanceFactory.getIsntance().getServiceId(),
						iServiceInstanceFactory.getIsntance().getEnvironment());
			}
			return;

		}
		jobDao.updateNoWatch(iServiceInstanceFactory.getIsntance().getInstanceId(), ids,
				iServiceInstanceFactory.getIsntance().getServiceId(),
				iServiceInstanceFactory.getIsntance().getEnvironment());
	}

	@Override
	public Job findByJobIdAndUse(Integer id) {
		return jobDao.findByJobIdAndUse(iServiceInstanceFactory.getIsntance().getInstanceId(), id);
	}

	@Override
	public boolean hasTask(Integer id) {
		return jobDao.hasTask(iServiceInstanceFactory.getIsntance().getInstanceId(), id) > 0;
	}

	@Override
	public List<Map<String, Object>> searchJob(Job job, ConcurrentSecurity securityJurisdiction) {
		return getSearchExecutor().searchForList(job, "ID,JOB_NAME",
				createBuilder("dispatchServer", securityJurisdiction), null, false);
	}

	@Autowired
	JobLogService jobLogService;

	@Override
	public Map<String, Object> getCount(String environment) {
		HashMap<String, Object> map = new HashMap<>();
		map.put("jobCount", jobDao.getJobCount(environment));
		map.put("serviceCount", jobDao.getServiceCount(environment));
		map.put("logCount", jobLogService.getLogCount(environment));
		return map;
	}

	@Override
	public List<String> searchJobService(Job job, ConcurrentSecurity securityJurisdiction) {
		return getSearchExecutor().searchForList(job, "distinct SERVICE_ID",
				createBuilder("dispatchServer", securityJurisdiction), null, false, String.class);

	}

	@Override
	public int searchCountByJobType(String name, String environment) {
		return jobDao.searchCountByJobType(name, environment);
	}

	@Autowired(required = false)
	TaskExecutorService taskExecutorService;

	@Override
	public boolean killAwaitJob(Integer jobId) {
		return jobLogService.killAwaitJob(jobId);
	}

	@Override
	public boolean endJob(Integer jobId, String jobLogId, String result, String msg, Integer status) {
		// 先获取对象 //后将此任务绑定到当前服务器//再执行endJob方法
		// 找到对应的服务器后发起停止操作
		Job job = jobDao.findById(jobId);
		if (job != null) {
			JobLog jobLog = jobLogService.findById(jobLogId);
			if (jobLog != null) {
				if (!job.getDispatchServer().equals(iServiceInstanceFactory.getIsntance().getInstanceId())) {
					updateWatch(jobId, iServiceInstanceFactory.getIsntance().getInstanceId(), job.getDispatchServer());
				}
				JobContext jobContext = new JobContext(new HashMap<>());
				jobContext.setChildrenJob(false);
				taskExecutorService.appendLog(jobLogId, "rpc连接异常主动结束任务!");
				taskExecutorService.endJob(job, jobLog, status, msg, result, jobContext);
				return true;
			}
			return false;
		}
		return false;
	}

	@Override
	public List<Job> findJobsByServiceId(String serviceId, String environment) {
		return jobDao.findJobsByServiceId(serviceId, environment);
	}

	@Override
	public List<Job> searchJobByArgs(String args) {
		return jobDao.searchJobByArgs(args);
	}

	@Override
	public int findCountJobByArgs(String args) {
		return jobDao.findCountJobByArgs(args);
	}

	@Override
	public List<String> findArgsById(String id) {
		return Arrays.asList(jobDao.findArgsById(id).split(","));
	}

	
	@Override
	public Map<String,Integer> getNormalStateTaskNumber(List<Integer> logStatus, Long startTime, Long endTime,
			List<Integer> jobStatus) {
		Integer activeTaskCount = jobDao.getActiveTaskCountByTime(logStatus, startTime, endTime,
				jobStatus);
		Integer allCount = jobDao.getallJobCountByStatus(jobStatus);
		Integer notActiveCount=allCount-activeTaskCount;
		Map<String,Integer> result=new HashMap<>();
		result.put("active", activeTaskCount);
		result.put("notActive", notActiveCount);
		return result;
	}
}
