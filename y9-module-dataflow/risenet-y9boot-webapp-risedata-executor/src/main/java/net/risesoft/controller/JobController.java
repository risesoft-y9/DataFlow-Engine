package net.risesoft.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import net.risedata.rpc.consumer.annotation.Listener;
import net.risedata.rpc.consumer.annotation.Listeners;
import net.risedata.rpc.utils.IdUtils;
import net.risesoft.api.ConfigApi;
import net.risesoft.model.Config;
import risesoft.data.transfer.core.Engine;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.listener.JobListener;
import risesoft.data.transfer.core.listener.impl.ResultJobListener;
import risesoft.data.transfer.core.statistics.Communication;
import risesoft.data.transfer.core.statistics.CommunicationTool;
import risesoft.data.transfer.core.statistics.State;
import risesoft.data.transfer.core.util.Configuration;

/**
 * 任务执行控制器
 * 
 * @typeName JobController
 * @date 2024年1月18日
 * @author lb
 */
@Listeners
@RestController
public class JobController {
	/**
	 * 执行任务的缓存配置
	 */
	private static Map<String, JobContext> JOB_CONTEXT = new ConcurrentHashMap<String, JobContext>();
	@Resource
	private ConfigApi configApi;

	@Listener(value = "executorJobs")
	public String executorJobs(String args) {
		// args 为多个id
		if (args == null) {
			throw new RuntimeException("系统错误 !  参数为空");
		}
		String[] ids = args.split(",");
		// 返回这些任务的
		for (String id : ids) {
			// await

		}
		return null;
	}

	/**
	 * 异步执行任务 返回任务id
	 * 
	 * @param context
	 * @return
	 */
	@PostMapping("/executorJobAsync")
	public String executorJobAsync(@RequestBody String context) {
		System.out.println(context);
		JobContext jobContext = Engine.start(IdUtils.getId() + "", Configuration.from(context), new JobListener() {
			@Override
			public void end(Communication communication) {
				if (communication.getState() == State.SUCCEEDED) {
					System.out.println(CommunicationTool.getStatistics(communication));
				} else {
					communication.getThrowable().printStackTrace();
					System.out.println("执行的条数" + communication.getLongCounter(CommunicationTool.READ_SUCCEED_RECORDS));
					System.out.println("失败" + communication.getThrowableMessage());
				}
			}
		}, null);
		JOB_CONTEXT.put(jobContext.getJobId(), jobContext);
		return jobContext.getJobId();
	}

	/**
	 * 同步执行任务返回任务的结果
	 * 
	 * @param context
	 * @return
	 */
	@PostMapping("/executorJob")
	public Map<String, Object> executorJob(@RequestBody String context) {
		Map<String, Object> result = new HashMap<String, Object>();
		ResultJobListener resultJobListener = new ResultJobListener();
		JobContext jobContext = Engine.start(IdUtils.getId() + "", Configuration.from(context), resultJobListener,
				null);
		result.put("success", resultJobListener.isSuccess());
		result.put("msg", resultJobListener.getMessage());
		return result;
	}

	/**
	 * 获取任务状态
	 * 
	 * @param jobId
	 * @return
	 */
	@GetMapping("/getStatus")
	public Map<String, Object> getStatus(String jobId) {
		Map<String, Object> result = new HashMap<String, Object>();
		JobContext jobContext = JOB_CONTEXT.get(jobId);
		if (jobContext == null) {
			result.put("success", false);
			result.put("msg", "任务不存在，或已经失效!");
		}
		Communication communication = jobContext.getCommunication();
		result.put("success", communication.getState() == State.SUCCEEDED);
		result.put("status", communication.getState());
		result.put("msg", communication.getState() == State.FAILED ? communication.getThrowableMessage()
				: CommunicationTool.getStatistics(communication));
		return result;
	}

	/**
	 * 停止任务
	 * 
	 * @param jobId
	 * @param message
	 * @return
	 */
	@GetMapping("/killJob")
	public Map<String, Object> killJob(String jobId, String message) {
		Map<String, Object> result = new HashMap<String, Object>();
		JobContext jobContext = JOB_CONTEXT.get(jobId);
		if (jobContext == null) {
			result.put("success", false);
			result.put("msg", "任务不存在，或已经失效!");
		} // 停止失败需要提供停止方法

		jobContext.getCommunication().setState(State.FAILED);
		jobContext.getCommunication().setThrowable(new RuntimeException(message));
		Engine.onJobFlush(jobContext);
		result.put("success", true);
		result.put("msg", "已停止!");
		return result;
	}

	// 任务是否需要起个名字 也可以

	@Listener("awaitExecutorJobs")
	public String awaitExecutorJobs(String args) {
		String[] ids = args.split(",");
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			System.out.println("获取任务id"+ids[i]);
			Config config = configApi.getConfig(ids[i]);
			if (config == null || config.getContent() == null) {
				throw new RuntimeException("任务" + ids[i] + "不存在配置,这通常的程序问题请联系开发人员!");
			}
			System.out.println("执行任务"+config.getContent());
			ResultJobListener resultJobListener = Engine.start(ids[i], Configuration.from(config.getContent()));
			if (resultJobListener.isSuccess()) {
				stringBuilder.append("任务:" + config.getName() + "执行成功\n" + resultJobListener.getMessage());
			} else {
				throw new RuntimeException(resultJobListener.getCommunication().getThrowable());
			}
		}
		System.out.println("返回" + stringBuilder);
		return stringBuilder.toString();
	}

}
