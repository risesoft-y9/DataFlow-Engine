package net.risesoft.controller.scheduler;

import net.risedata.jdbc.commons.utils.DateUtils;
import net.risedata.jdbc.commons.utils.GroupUtils;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.aop.CheckHttpForArgs;
import net.risesoft.api.job.log.LogAnalyseService;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.persistence.model.log.LogAnalyse;
import net.risesoft.controller.BaseController;
import net.risesoft.pojo.Y9Result;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.text.DateFormat;
import java.util.*;

/**
 * @Description : 任务调度日志控制器
 * @ClassName JobController
 * @Author lb
 * @Date 2022/8/30 15:57
 * @Version 1.0
 */
@RestController()
@RequestMapping("/api/rest/job/log")
public class JobLogController extends BaseController {
	@InitBinder
	public void initBinder(WebDataBinder binder, WebRequest request) {
		// 转换日期

		DateFormat dateFormat = DateUtils.getFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		// 自定义自己的date转换器springmvc默认是不支持date对象的转换当遇到date对象的时候会周此方法 是类独有的
	}

	@Autowired
	JobLogService jobLogService;

	/**
	 * 根据id 获取任务信息
	 *
	 * @param id 任务id
	 * @return
	 */
	@GetMapping("getOne")
	public Y9Result<Object> getOne(String id) {
		return Y9Result.success(jobLogService.findById(id));
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
	public Y9Result<Object> search(JobLog job, LPageable page, String environment, String jobType, Integer[] jobIds) {
		return Y9Result.success(jobLogService.search(job, page, getSecurityJurisdiction(environment), jobType, jobIds));
	}

	@GetMapping("getConsole")
	public Y9Result<Object> getConsole(String id) {
		return Y9Result.success(jobLogService.findConsoleById(id));
	}

	@GetMapping("/searchByGroup")
	public Y9Result<Object> searchByGroup(@RequestParam(required = true) Date startDate,
			@RequestParam(required = true) Date endDate, String environment, LPageable page, String jobName) {
		return Y9Result.success(jobLogService.searchByGroup(startDate, endDate, environment, page, jobName));
	}

	@Autowired
	private List<LogAnalyseService> logAnalyseServices;

	/**
	 * 出具一份数据报告
	 * 
	 * @param startDate
	 * @param endDate
	 * @param environment
	 * @return
	 */
	@GetMapping("/searchByGroupReport")
	public Y9Result<Object> searchByGroupReport(Date startDate, Date endDate, String environment, String jobName) {
		List<Map<String, Object>> logList = jobLogService.searchByGroupLog(startDate, endDate, environment, jobName);
		List<LogAnalyse> logAnalyseList = new ArrayList<>();
		int size;
		Integer jobId;
		String log, jobName2;
		for (Map<String, Object> map : logList) {
			jobId = Integer.parseInt(map.get("JOB_ID").toString());
			if (map.get("JOB_NAME") == null) {
				continue;
			}
			jobName2 = map.get("JOB_NAME").toString();
			log = map.get("LOG_CONSOLE").toString();
			if (StringUtils.isBlank(jobName2)) {
				continue;
			}
			for (LogAnalyseService analyseService : logAnalyseServices) {
				// 处理一个就关闭一个
				size = logAnalyseList.size();
				analyseService.doAnalyse(jobId, log, jobName2, logAnalyseList,map);
				if (size == logAnalyseList.size()) {
					logAnalyseList.add(new LogAnalyse(jobId, jobName2, "其他", log, "未分析的原因",Integer.parseInt( map.get("JOB_END_STATUS").toString())));
				}
			}
		}
		Map<String, List<LogAnalyse>> result = GroupUtils.group(logAnalyseList, (item) -> {
			return item.getType();
		});
		Map<String, Map<String, List<LogAnalyse>>> res = new HashMap<>();
		for (String key : result.keySet()) {
			res.put(key, GroupUtils.group(result.get(key), (item) -> {
				return item.getMsg();
			}));
		}
		return Y9Result.success(res);
	}
}
