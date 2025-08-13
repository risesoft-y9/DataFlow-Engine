package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.job.JobService;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.security.ConcurrentSecurity;
import net.risesoft.security.SecurityManager;
import net.risesoft.security.service.EnvironmentService;
import net.risesoft.service.HomeDataService;
import net.risesoft.util.DataServiceUtil;

@Service(value = "homeDataService")
@RequiredArgsConstructor
public class HomeDataServiceImpl implements HomeDataService {

	private final JobLogService jobLogService;

	private final JobService jobService;

	private final EnvironmentService environmentService;
	
	private final SecurityManager securityManager;

	@Override
	public Y9Result<Map<String, Object>> getCurrentTaskInfo(String type) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			// 获取环境列表
			map.put("allEnvironments", environmentService.findAll());
			// 查看权限
			ConcurrentSecurity security = securityManager.getConcurrentSecurity();
			// 获取全部任务数量
			map.put("allTask", jobService.countByEnvironmentAndJobType(type, security.getJobTypes(), security.isSystemManager()));
			// 今日已执行任务数
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			long[] timestamps = DataServiceUtil.getDayTimestamps(sdf.format(new Date()));
			// 获取成功数
			Integer scount = jobLogService.getJobCount(Arrays.asList(JobLog.SUCCESS), timestamps[0], timestamps[1],
					type, security.getJobTypes(), security.isSystemManager());
			// 获取失败数
			Integer ecount = jobLogService.getJobCount(Arrays.asList(JobLog.ERROR), timestamps[0], timestamps[1],
					type, security.getJobTypes(), security.isSystemManager());
			map.put("doneTask", scount + ecount + "（"+scount+"/"+ecount+"）");
			// 正在执行任务数
			map.put("doingTask", jobLogService.getCount(Arrays.asList(JobLog.START), type, security.getJobTypes(), security.isSystemManager()));
			// 执行成功数
			map.put("successTask", jobLogService.getCount(Arrays.asList(JobLog.SUCCESS), type, security.getJobTypes(), security.isSystemManager()));
			// 执行失败数
			map.put("errorTask", jobLogService.getCount(Arrays.asList(JobLog.ERROR), type, security.getJobTypes(), security.isSystemManager()));
			// 等待执行数
			map.put("waitTask", jobLogService.getCount(Arrays.asList(JobLog.AWAIT), type, security.getJobTypes(), security.isSystemManager()));
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("获取当前任务运行情况失败：" + e.getMessage());
		}
		return Y9Result.success(map);
	}

	@Override
	public Y9Result<Map<String, Object>> getDailySchedulingFrequencyInfo(String type) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			// 查看权限
			ConcurrentSecurity security = securityManager.getConcurrentSecurity();
			List<Integer> frequencyList = new ArrayList<Integer>();
			// 获取近一周的日期时间戳
			List<String> dateList = DataServiceUtil.getNearlyWeek();
			for(String date : dateList) {
				long[] timestamps = DataServiceUtil.getDayTimestamps(date);
				// 获取当天的调度次数
				Integer count = jobLogService.getFrequencyCount(timestamps[0], timestamps[1], type, security.getJobTypes(), security.isSystemManager());
				frequencyList.add(count);
			}
			result.put("dateList", dateList);
			result.put("frequencyList", frequencyList);
		} catch (Exception e) {
			e.getStackTrace();
			return Y9Result.failure("获取每日调度次数失败：" + e.getMessage());
		}
		return Y9Result.success(result);
	}

	@Override
	public List<Map<String, Object>> getTaskStateInfo(String type) {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		try {
			// 查看权限
			ConcurrentSecurity security = securityManager.getConcurrentSecurity();
			// 获取定时任务数
			int taskCount1 = jobService.getTaskCountByStatus(type, 1, security.getJobTypes(), security.isSystemManager());
			// 获取非定时任务数
			int taskCount2 = jobService.getTaskCountByStatus(type, 0, security.getJobTypes(), security.isSystemManager());
			// 创建listMap
			result = Stream.of(createDataMap("定时", taskCount1), createDataMap("非定时", taskCount2)).collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private Map<String, Object> createDataMap(String name, Integer value) {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("name", name);
		dataMap.put("value", value);
		return dataMap;
	}

	@Override
	public Map<String, Object> getSchedulingInfo(String type) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
			// 查看权限
			ConcurrentSecurity security = securityManager.getConcurrentSecurity();
			List<Integer> successCount = new ArrayList<Integer>();
			List<Integer> errorCount = new ArrayList<Integer>();
			// 获取近一周的日期时间戳
			List<String> dateList = DataServiceUtil.getNearlyWeek();
			for(String date : dateList) {
				long[] timestamps = DataServiceUtil.getDayTimestamps(date);
				// 获取当天的调度成功数
				Integer count1 = jobLogService.getJobCount(Arrays.asList(JobLog.SUCCESS), timestamps[0], timestamps[1], type,
						security.getJobTypes(), security.isSystemManager());
				successCount.add(count1);
				// 获取当天的调度失败数
				Integer count2 = jobLogService.getJobCount(Arrays.asList(JobLog.ERROR), timestamps[0], timestamps[1], type,
						security.getJobTypes(), security.isSystemManager());
				errorCount.add(count2);
			}
			result.put("dateList", dateList);

			Map<String, Object> sMap = new HashMap<String, Object>();
			sMap.put("name", "成功");
			sMap.put("data", successCount);
			listMap.add(sMap);

			Map<String, Object> eMap = new HashMap<String, Object>();
			eMap.put("name", "失败");
			eMap.put("data", errorCount);
			listMap.add(eMap);

			result.put("taskScheduLingInfo", listMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

}
