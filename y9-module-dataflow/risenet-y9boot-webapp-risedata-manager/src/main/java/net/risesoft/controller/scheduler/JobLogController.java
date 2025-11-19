package net.risesoft.controller.scheduler;

import net.risedata.jdbc.commons.LPage;
import net.risedata.jdbc.commons.utils.DateUtils;
import net.risedata.jdbc.commons.utils.GroupUtils;
import net.risedata.jdbc.search.LPageable;
import net.risesoft.api.aop.CheckHttpForArgs;
import net.risesoft.api.job.log.LogAnalyseService;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.api.persistence.model.log.LogAnalyse;
import net.risesoft.controller.BaseController;
import net.risesoft.dto.JobLogDTO;
import net.risesoft.pojo.Y9Result;
import net.risesoft.security.ConcurrentSecurity;
import net.risesoft.service.DataBusinessService;
import net.risesoft.service.JobDoActionService;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;


import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description : 任务调度日志控制器
 * @ClassName JobController
 * @Author lb
 * @Date 2022/8/30 15:57
 * @Version 1.0
 */
@RestController()
@RequestMapping("/api/rest/job/log")
public class JobLogController extends BaseController  {

	@InitBinder
	public void initBinder(WebDataBinder binder, WebRequest request) {
		// 转换日期

		DateFormat dateFormat = DateUtils.getFormat("yyyy-MM-dd");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
		// 自定义自己的date转换器springmvc默认是不支持date对象的转换当遇到date对象的时候会周此方法 是类独有的
	}

	@Autowired
	private JobLogService jobLogService;

	@Autowired
	private DataBusinessService dataBusinessService;

	@Autowired
	private JobDoActionService jobDoActionService;
	
	@Autowired
	private ModelMapper modelMapper;

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
	 * 对任务执行操作事件
	 * 
	 * @param body
	 * @return
	 */
	@PostMapping("/doOperationByLogId")
	public Y9Result<String> doOperationByLogId(@RequestBody String body) {
		return jobDoActionService.doAction(body);
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
	public Y9Result<Object> search(JobLogDTO jobLogDTO, LPageable page, String environment, String jobType, Integer[] jobIds) {
		ConcurrentSecurity jurisdiction = null;
		try {
			JobLog job = modelMapper.map(jobLogDTO, JobLog.class);
			jurisdiction = getSecurityJurisdiction(environment);
			LPage<Map<String, Object>> pages = jobLogService.search(job, page, jurisdiction, jobType, jobIds);
			pages.getContent().stream().map((item) -> {
				item.put("JOB_TYPE_NAME", dataBusinessService.getNameById((String) item.get("JOB_TYPE")));
				return item;
			}).collect(Collectors.toList());
			return Y9Result.success(pages);
		} catch (Exception e) {
			return Y9Result.failure(e.getMessage());
		}
	}

	@GetMapping("getConsole")
	public Y9Result<Object> getConsole(String id) {
		return Y9Result.success(jobLogService.findConsoleById(id));
	}

	@GetMapping("/searchByGroup")
	public Y9Result<Object> searchByGroup(@RequestParam(required = true) Date startDate,
			@RequestParam(required = true) Date endDate, String environment, LPageable page, String jobName) {
		ConcurrentSecurity jurisdiction = null;
		try {
			jurisdiction = getSecurityJurisdiction(environment);
		} catch (Exception e) {
			return Y9Result.failure(e.getMessage());
		}
		return Y9Result
				.success(jobLogService.searchByGroup(startDate, endDate, environment, page, jobName, jurisdiction));
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
				analyseService.doAnalyse(jobId, log, jobName2, logAnalyseList, map);
				if (size == logAnalyseList.size()) {
					logAnalyseList.add(new LogAnalyse(jobId, jobName2, "其他", log, "未分析的原因",
							Integer.parseInt(map.get("JOB_END_STATUS").toString())));
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
