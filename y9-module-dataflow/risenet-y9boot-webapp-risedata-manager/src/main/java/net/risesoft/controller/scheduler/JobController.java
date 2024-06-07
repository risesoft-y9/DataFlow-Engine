package net.risesoft.controller.scheduler;

import net.risedata.jdbc.factory.ObjectBuilderFactory;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.aop.CheckHttpForArgs;
import net.risesoft.api.job.TaskExecutorService;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.job.JobService;
import net.risesoft.api.persistence.model.job.Job;
import net.risesoft.api.utils.Sort;
import net.risesoft.api.utils.TaskUtils;
import net.risesoft.controller.BaseController;
import net.risesoft.pojo.Y9Result;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @Description : 任务控制器
 * @ClassName JobController
 * @Author lb
 * @Date 2022/8/30 15:57
 * @Version 1.0
 */
@RestController()
@RequestMapping("/api/rest/job/data")
public class JobController extends BaseController {

	@Autowired
	JobService jobService;

	@GetMapping("/searchJob")
	public Y9Result<Object> searchJob(String environment, Job job) {
		return Y9Result.success(jobService.searchJob(job, getSecurityJurisdiction(environment)));
	}

	@GetMapping("/searchJobService")
	public Y9Result<Object> searchJobService(String environment) {
		return Y9Result.success(
				jobService.searchJobService(ObjectBuilderFactory.builder(Job.class).builder("environment", environment),
						getSecurityJurisdiction(environment)));
	}

	/**
	 * 根据id 获取任务信息
	 *
	 * @param id 任务id
	 * @return
	 */
	@GetMapping("getOne")
	public Y9Result<Object> getOne(Integer id) {
		return Y9Result.success(jobService.findByJobId(id));
	}

	/**
	 * 查询
	 *
	 * @param job
	 * @param page
	 * @return
	 */
	@CheckHttpForArgs
	@GetMapping("search")
	public Y9Result<Object> search(Job job, LPageable page) {
		return Y9Result.success(jobService.search(job, page, getSecurityJurisdiction(job.getEnvironment())));
	}

	/**
	 * 删除任务
	 *
	 * @param id
	 * @return
	 */
	@PostMapping("delete")
	public Y9Result<Object> delete(Integer id) {
		jobService.deleteByJobId(id);
		return Y9Result.success("删除成功");
	}

	/**
	 * 修改状态
	 *
	 * @param id
	 * @return
	 */

	@PostMapping("setStatus")
	public Y9Result<Object> setStatus(Integer id, int status) {
		jobService.setStatus(id, status);
		return Y9Result.success(status);
	}

	/**
	 * 配置文件
	 *
	 * @param job
	 * @return
	 */
	@PostMapping("save")
	@CheckHttpForArgs
	public Y9Result<Object> save(@RequestBody @Valid Job job, BindingResult result) {
		jobService.saveJob(job);
		return Y9Result.success(job.getId());
	}

	@Autowired(required = false)
	TaskExecutorService taskExecutorService;

	@Autowired
	JobLogService jobLogService;

	/**
	 * 指定服务并调度
	 *
	 * @param jobId  任务id
	 * @param args   参数
	 * @param server 指定服务
	 * @return
	 */
	@PostMapping("sendJob")
	public Y9Result<Object> sendJob(Integer jobId, String args, String server, String dispatchArgs) {

		Job byJobId = jobService.findByJobId(jobId);
		if (byJobId != null) {
			if (!StringUtils.isBlank(args)) {
				byJobId.setArgs(args);
			}
			if (!StringUtils.isBlank(server)) {
				byJobId.setDispatchMethod("指定");
			}
			if (!StringUtils.isBlank(dispatchArgs)) {
				byJobId.setDispatchArgs(dispatchArgs);
			}

			String logId = taskExecutorService.startJob(byJobId, server);
			return Y9Result.success(logId);
		} else {
			return Y9Result.failure("没有找到此任务");
		}
	}

	/**
	 * 获取下10次执行的时间
	 *
	 * @param jobId
	 * @return
	 */
	@PostMapping("nextExecutorTime")
	public Y9Result<Object> nextExecutorTime(@RequestParam(required = true) Integer jobId) {
		List<String> res = new ArrayList<>();
		try {
			Job job = jobService.findByJobId(jobId);
			if (job == null) {
				return Y9Result.failure("未找到任务");
			}
			String dispatchType = job.getDispatchType();
			if (dispatchType.equals("cron")) {
				LocalDateTime dateTime = LocalDateTime.now();
				for (int i = 0; i < 10; i++) {
					dateTime = CronExpression.parse(job.getSpeed()).next(dateTime);
					res.add(dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
				}
			} else if (dispatchType.equals("固定速度")) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date date = new Date();
				int speed = Integer.parseInt(job.getSpeed()) * 1000;
				for (int i = 0; i < 10; i++) {
					date = new Date(date.getTime() + speed);
					res.add(sdf.format(date));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Y9Result.success(res);
	}

	@RequestMapping("/getCount")
	public Y9Result<Object> getCount(String environment) {
		return Y9Result.success(jobService.getCount(environment));
	}

	/**
	 * 删除掉这个任务正在运行的任务
	 *
	 * @param jobId
	 * @return
	 */
	@PostMapping("/killAwaitJob")
	public Y9Result<Object> killAwaitJob(Integer jobId) {
		return Y9Result.success(jobService.killAwaitJob(jobId));
	}

	/**
	 * 停止任务 暂未实现 需要调度端提供相应接口
	 * 
	 * @param jobId
	 * @param jobLogId
	 * @param result
	 * @param msg
	 * @param status
	 * @return
	 */
	@PostMapping("/endJob")
	public Boolean endJob(Integer jobId, String jobLogId, String result, String msg, Integer status) {
		return jobService.endJob(jobId, jobLogId, result, msg, status);
	}

	/**
	 * 根据任务项id获取任务
	 * 
	 * @param taskId
	 * @return
	 */
	@RequestMapping("/getJobsByTaskId")
	public Y9Result<List<Job>> getJobsByTaskId(@RequestParam(required = true) String taskId) {
		return Y9Result.success(jobService.searchJobByArgs(taskId));
	}

	/**
	 * 生成任务忙碌图
	 * 
	 * @param serviceId
	 * @param environment
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping("/showJobView")
	public Y9Result<Object> showJobView(String serviceId, String environment) throws ParseException {
		List<Job> jobs = jobService.findJobsByServiceId(serviceId, environment);
		if (jobs.size() > 0) {
			Map<String, Integer> map = new HashMap<>();
			List<List<String>> result = new ArrayList<>();
			Integer count;
			List<String> res;
			for (Job job : jobs) {
				if (job.getDispatchType().equals("cron")) {
					res = TaskUtils.getDayTaskOfCron(job.getSpeed(), null);
				} else {
					res = TaskUtils.getDayTaskOfSpped(Integer.parseInt(job.getSpeed()), null);
				}
				result.add(res);
				for (String d : res) {
					count = map.get(d);
					if (count == null) {
						count = 0;
					}
					count++;
					map.put(d, count);
				}
			}
			// 根据count 将key 归类后排序
			ArrayList<Sort> sorts = new ArrayList<>();
			map.forEach((k, v) -> {
				sorts.add(new Sort(v, k));
			});
			sorts.sort(Sort::compareTo);
			int startIndex = -1;
			int endIndex = -1;
			List<Sort> sorts1 = new ArrayList<>();
			for (int i = 0; i < sorts.size(); i++) {
				if (startIndex == -1) {
					startIndex = i;
					endIndex = i;
				} else {
					if (sorts.get(startIndex).count == sorts.get(i).count) {
						endIndex = i;
					} else {
						sorts1.add(new Sort(sorts.get(startIndex).count, doSortName(startIndex, endIndex, sorts)));
						startIndex = i;
						endIndex = i;
					}
				}
			}
			sorts1.add(new Sort(sorts.get(startIndex).count, doSortName(startIndex, endIndex, sorts)));
			return Y9Result.success(sorts1);
		}
		return Y9Result.failure("无启动的任务");
	}

	private static String doSortName(int startIndex, int endIndex, List<Sort> sorts) {
		if (startIndex != endIndex) {
			return sorts.get(startIndex).name + "至" + sorts.get(endIndex).name;
		} else {
			return sorts.get(startIndex).name;
		}
	}

}
