package net.risesoft.listener;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.job.JobService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataArrangeLogEntity;
import net.risesoft.y9public.entity.DataProcessEntity;
import net.risesoft.y9public.repository.DataArrangeLogRepository;

@Component
@RequiredArgsConstructor
public class ArrangeExecuteListener {
	
	private final TaskExecutorService taskExecutorService;
	private final JobService jobService;
	private final JobLogService jobLogService;
	private final DataArrangeLogRepository dataArrangeLogRepository;
	
	@Async
	public void executeJob(String id, List<DataProcessEntity> processList, Integer identifier) {
		try {
			for(DataProcessEntity dataProcess : processList) {
				Integer gateway = dataProcess.getGateway();
				if(gateway == 1) {// 与
					// 执行任务并获取返回结果，返回false就中断接下来的任务
					boolean result = andGateway(dataProcess.getJobIds(), id, dataProcess.getId(), identifier);
					if(!result) {
						break;
					}
				}else if(gateway == 2) {// 或
					boolean result = orGateway(dataProcess.getJobIds(), id, dataProcess.getId(), identifier);
					if(!result) {
						break;
					}
				}else {// 无网关直接执行任务
					boolean result = noGateway(dataProcess.getJobIds(), id, dataProcess.getId(), identifier);
					if(!result) {
						break;
					}
				}
				// 判断有没需要执行的副节点任务
				if(StringUtils.isNotBlank(dataProcess.getSubJobs())) {
					CompletableFuture.runAsync(() -> subJobs(dataProcess.getSubJobs(), id, dataProcess.getId(), identifier));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private boolean checkJobStatus(String logId) {
		// 获取状态
		int status = getJobLogStatus(logId);
    	while (status == 0) {
    		try {
    			status = getJobLogStatus(logId);
    			// 等待60秒再执行
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return status == 1 ? true : false;
	}
	
	private int getJobLogStatus(String logId) {
		JobLog jobLog = jobLogService.findById(logId);
		return jobLog.getStatus();
	}
	
	private boolean sendJob(Map<String, Object> map, String arrangeId, String processId, Integer identifier) {
		String server = "";
		if(map.get("executNode") != null) {
			server = (String)map.get("executNode");
		}
		Job job = jobService.findByJobId(Integer.parseInt(map.get("taskId").toString()));
		if (job != null) {
			if (StringUtils.isNotBlank(server)) {
				job.setDispatchMethod("指定");
			}
			// 执行任务
			String logId = taskExecutorService.startJob(job, server);
			// 记录初始日志信息
			this.saveJobLog(arrangeId, processId, job.getId(), job.getName(), logId, "", identifier);
			// 根据日志id获取任务执行进度
			return checkJobStatus(logId);
		}
		return false;
	}
	
	private void saveJobLog(String arrangeId, String processId, Integer jobId, String jobName, String jobLogId, String errorMsg, Integer identifier) {
		DataArrangeLogEntity dataArrangeLogEntity = dataArrangeLogRepository.
				findByArrangeIdAndProcessIdAndJobIdAndJobLogId(arrangeId, processId, jobId, jobLogId);
		if(dataArrangeLogEntity == null) {
			dataArrangeLogEntity = new DataArrangeLogEntity();
			dataArrangeLogEntity.setId(Y9IdGenerator.genId());
		}
		dataArrangeLogEntity.setArrangeId(arrangeId);
		dataArrangeLogEntity.setJobId(jobId);
		dataArrangeLogEntity.setJobLogId(jobLogId);
		dataArrangeLogEntity.setJobName(jobName);
		dataArrangeLogEntity.setProcessId(processId);
		dataArrangeLogEntity.setErrorMsg(errorMsg);
		dataArrangeLogEntity.setIdentifier(identifier);
		dataArrangeLogRepository.save(dataArrangeLogEntity);
	}
	
	/**
	 * 无网关执行任务：限单任务，不能存在并行任务
	 * @param jobIds
	 * @return
	 */
	private boolean noGateway(String jobIds, String arrangeId, String processId, Integer identifier) {
		List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(jobIds);
		if(listMap.size() > 1) {
			this.saveJobLog(arrangeId, processId, null, "", "", "执行失败：无网关节点任务不能是多个并行任务，只能是单任务执行", identifier);
			return false;
		}
		boolean hasSuccess = sendJob(listMap.get(0), arrangeId, processId, identifier);
		return hasSuccess;
	}
	
	/**
	 * 并行（与）网关
	 * @param jobIds
	 * @return
	 */
	private boolean andGateway(String jobIds, String arrangeId, String processId, Integer identifier) {
		// 并行执行主任务
		List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(jobIds);
		List<CompletableFuture<Boolean>> jobFutures = listMap.stream().map(n -> CompletableFuture.supplyAsync(()
				-> sendJob(n, arrangeId, processId, identifier))).collect(Collectors.toList());
		// 等待所有任务执行完并收集结果
		CompletableFuture.allOf(jobFutures.toArray(new CompletableFuture[0])).join();
		List<Boolean> jobResults = jobFutures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 判断任务结果里是否有false
		boolean hasFalse = jobResults.stream().anyMatch(Boolean.FALSE::equals);
		if(hasFalse) {
			return false;
		}
		return true;
	}
	
	/**
	 * 并行（或）网关
	 * @param jobIds
	 * @return
	 */
	private boolean orGateway(String jobIds, String arrangeId, String processId, Integer identifier) {
		try {
			// 并行执行主任务
			List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(jobIds);
			List<CompletableFuture<Boolean>> jobFutures = listMap.stream().map(n -> CompletableFuture.supplyAsync(()
					-> sendJob(n, arrangeId, processId, identifier))).collect(Collectors.toList());
			// 任意一个任务完成就返回结果去执行下一步
			CompletableFuture<Object> anyFuture = CompletableFuture.anyOf(jobFutures.toArray(new CompletableFuture[0]));
			// 获取结果
			Object result = anyFuture.get();
			return Boolean.parseBoolean(result.toString());
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 处理副节点任务
	 * @param subJobIds
	 */
	private void subJobs(String subJobIds, String arrangeId, String processId, Integer identifier) {
		List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(subJobIds);
		for(Map<String, Object> map : listMap) {
			boolean hasSuccess = sendJob(map, arrangeId, processId, identifier);
			// 任务失败直接中断
			if(!hasSuccess) {
				break;
			}
		}
	}

}
