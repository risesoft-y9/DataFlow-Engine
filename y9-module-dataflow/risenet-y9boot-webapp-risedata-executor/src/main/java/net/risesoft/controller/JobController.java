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
import net.risedata.rpc.consumer.listener.ListenerDispatch;
import net.risedata.rpc.consumer.result.SyncResult;
import net.risedata.rpc.utils.IdUtils;
import net.risesoft.api.ConfigApi;
import net.risesoft.model.Config;
import net.risesoft.service.HandleSingleJobService;
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
		if (!resultJobListener.isSuccess()) {

			resultJobListener.getCommunication().getThrowable().printStackTrace();
		}
		return result;
	}

	/**
	 * 获取任务状态
	 * 
	 * @param jobId
	 * @return
	 */
	@GetMapping("/getStatus")
	@Listener("getStatus")
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
	 * 停止
	 * 
	 * @param jobId
	 * @return
	 */
	@Listener("stop")
	@GetMapping("/stop")
	public Map<String, Object> stop(String jobId, String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		JobContext jobContext = JOB_CONTEXT.get(jobId);
		if (jobContext == null) {
			result.put("success", false);
			result.put("msg", "任务不存在，或已经失效!");
		}
		jobContext.stop(new RuntimeException(msg));
		Communication communication = jobContext.getCommunication();
		result.put("success", communication.getState() == State.SUCCEEDED);
		result.put("status", communication.getState());
		result.put("msg", communication.getState() == State.FAILED ? communication.getThrowableMessage()
				: CommunicationTool.getStatistics(communication));
		return result;
	}

	/**
	 * 暂停
	 * @param jobId
	 * @param msg
	 * @return
	 */
	@Listener("await")
	@GetMapping("/await")
	public Map<String, Object> await(String jobId, String msg) {
		Map<String, Object> result = new HashMap<String, Object>();
		JobContext jobContext = JOB_CONTEXT.get(jobId);
		if (jobContext == null) {
			result.put("success", false);
			result.put("msg", "任务不存在，或已经失效!");
		}
		jobContext.await(msg);
		Communication communication = jobContext.getCommunication();
		result.put("success", communication.getState() == State.SUCCEEDED);
		result.put("status", communication.getState());
		result.put("msg", communication.getState() == State.FAILED ? communication.getThrowableMessage()
				: CommunicationTool.getStatistics(communication));
		return result;
	}
	/**
	 * 启动
	 * @param jobId
	 * @param msg
	 * @return
	 */
	@GetMapping("/runing")
	@Listener("runing")
	public Map<String, Object> runing(String jobId) {
		Map<String, Object> result = new HashMap<String, Object>();
		JobContext jobContext = JOB_CONTEXT.get(jobId);
		if (jobContext == null) {
			result.put("success", false);
			result.put("msg", "任务不存在，或已经失效!");
		}
		jobContext.running();
		Communication communication = jobContext.getCommunication();
		result.put("success", communication.getState() == State.SUCCEEDED);
		result.put("status", communication.getState());
		result.put("msg", communication.getState() == State.FAILED ? communication.getThrowableMessage()
				: CommunicationTool.getStatistics(communication));
		return result;
	}
	


	@Listener("awaitExecutorJobs")
	public String awaitExecutorJobs(String args,String jobLogId) {

		String[] ids = args.split(",");
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			System.out.println("获取任务id：" + ids[i]);
			// 获取任务类型
			Integer taskType = configApi.taskType(ids[i]);
			if (taskType == 1) {// 单节点任务
				Config config = configApi.getSingleConfig(ids[i]);
				if (config == null || config.getContent() == null) {
					throw new RuntimeException("任务" + ids[i] + "不存在配置");
				}
				// 执行任务
				String msg = HandleSingleJobService.handle(config);
				stringBuilder.append("任务:" + config.getName() + "\n" + msg);
			} else {// 同步任务
				Config config = configApi.getConfig(ids[i]);
				if (config == null || config.getContent() == null) {
					throw new RuntimeException("任务" + ids[i] + "不存在配置,这通常的程序问题请联系开发人员!");
				}
				System.out.println("执行任务" + config.getContent());
				ResultJobListener resultJobListener = Engine.start(ids[i], Configuration.from(config.getContent()));
                JOB_CONTEXT.put(jobLogId, resultJobListener.getJobContext());
				if (resultJobListener.isSuccess()) {
					stringBuilder.append("任务:" + config.getName() + "执行成功\n" + resultJobListener.getMessage());
				} else {
					throw new RuntimeException(resultJobListener.getCommunication().getThrowable());
				}
			}
		}
//		new syncres

		System.out.println(stringBuilder);
		return stringBuilder.toString();
	}

}
