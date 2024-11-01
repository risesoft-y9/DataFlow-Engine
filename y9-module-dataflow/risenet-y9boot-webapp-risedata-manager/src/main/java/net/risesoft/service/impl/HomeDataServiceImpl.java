package net.risesoft.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import net.risesoft.util.home.QueryTimeRangeCacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risedata.jdbc.commons.utils.DateUtils;
import net.risesoft.api.persistence.job.JobLogService;
import net.risesoft.api.persistence.job.JobService;
import net.risesoft.api.persistence.model.job.JobLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.pojo.home.HomeData;
import net.risesoft.pojo.home.HomeQueryModel;
import net.risesoft.pojo.home.HomeData.CurrentTaskInfo;
import net.risesoft.pojo.home.HomeData.DailySchedulingFrequencyInfo;
import net.risesoft.pojo.home.HomeData.JobLogInfo;
import net.risesoft.pojo.home.HomeData.LogGroupInfo;
import net.risesoft.pojo.home.HomeData.SchedulingInfo;
import net.risesoft.pojo.home.HomeQueryModel.CurrentTaskQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.DailySchedulingFrequencyQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.JobLogQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.SchedulingQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.TaskStateQueryInfo;
import net.risesoft.security.model.Environment;
import net.risesoft.security.service.EnvironmentService;
import net.risesoft.service.HomeDataService;
import net.risesoft.util.ExceptionUtils;
import net.risesoft.util.JobLogStatusEnum;

@Service(value = "homeDataService")
@RequiredArgsConstructor
@Slf4j
public class HomeDataServiceImpl implements HomeDataService {

	@Resource(name = "homeDataExecutor")
	ThreadPoolTaskExecutor homeDataExecutor;

	JobLogService jobLogService;

	JobService jobService;

	EnvironmentService environmentService;

	@Autowired
	public HomeDataServiceImpl(JobLogService jobLogService, JobService jobService, EnvironmentService environmentService) {
		this.jobLogService = jobLogService;
		this.jobService = jobService;
		this.environmentService = environmentService;
	}

	@Override
	public Y9Result<HomeData> getHomeDataSync() {
		HomeData homeData = new HomeData();
		HomeQueryModel homeQueryModel = new HomeQueryModel();
		try {

			CurrentTaskInfo currentTaskInfo = getCurrentTaskInfo(homeQueryModel.getCurrentTaskQueryInfo());

			DailySchedulingFrequencyInfo dailySchedulingFrequencyInfo = getDailySchedulingFrequencyInfo(
					homeQueryModel.getDailySchedulingFrequencyQueryInfo());

			List<Map<String, Object>> TaskStateInfo = getTaskStateInfo(homeQueryModel.getTaskStateQueryInfo());

			List<Environment> environmentList = environmentService.findAll();

			homeData.setAllEnvironments(environmentList);
			homeQueryModel.getSchedulingQueryInfo().setEnvironment(Optional.ofNullable(environmentList)
					.orElse(Collections.emptyList()).stream().findFirst().map(Environment::getName).orElse(""));
			SchedulingInfo schedulingInfo = getSchedulingInfo(homeQueryModel.getSchedulingQueryInfo());

			JobLogInfo jobLogInfo = getJobLogInfo(homeQueryModel.getJobLogQueryInfo());

			homeData.setSchedulingInfo(schedulingInfo);

			homeData.setCurrentTaskInfo(currentTaskInfo);
			homeData.setDailySchedulingFrequencyInfo(dailySchedulingFrequencyInfo);
			homeData.setTaskStateInfo(TaskStateInfo);
			homeData.setJobLogInfo(jobLogInfo);

		} catch (Exception e) {
			LOGGER.error("获取首页数据失败", ExceptionUtils.extractConcurrentException(e));
			return Y9Result.failure("获取首页数据失败");
		}

		return Y9Result.success(homeData, "获取首页数据成功");
	}

	@Override
	public Y9Result<HomeData> getHomeData() {
		HomeData homeData = new HomeData();
		HomeQueryModel homeQueryModel = new HomeQueryModel();
		try {

			CompletableFuture<CurrentTaskInfo> currentTaskInfo = createCurrentTaskInfo(
					homeQueryModel.getCurrentTaskQueryInfo());

			CompletableFuture<DailySchedulingFrequencyInfo> dailySchedulingFrequencyInfo = CompletableFuture
					.supplyAsync(() -> getDailySchedulingFrequencyInfo(
							homeQueryModel.getDailySchedulingFrequencyQueryInfo()), homeDataExecutor);

			CompletableFuture<List<Map<String, Object>>> TaskStateInfo = CompletableFuture
					.supplyAsync(() -> getTaskStateInfo(homeQueryModel.getTaskStateQueryInfo()), homeDataExecutor);

			CompletableFuture<Void> schedulingInfo = CompletableFuture
					.supplyAsync(() -> environmentService.findAll(), homeDataExecutor).thenAccept(data -> {

						homeData.setAllEnvironments(data);
						homeQueryModel.getSchedulingQueryInfo()
								.setEnvironment(Optional.ofNullable(data).orElse(Collections.emptyList()).stream()
										.findFirst().map(Environment::getName).orElse(""));
					}).thenCompose(data -> CompletableFuture.supplyAsync(
							() -> getSchedulingInfo(homeQueryModel.getSchedulingQueryInfo()), homeDataExecutor))

					.thenAccept(data -> homeData.setSchedulingInfo(data));

			CompletableFuture<Void> jobLogInfo = CompletableFuture
					.supplyAsync(() -> getJobLogInfo(homeQueryModel.getJobLogQueryInfo()), homeDataExecutor)
					.thenAccept(data -> homeData.setJobLogInfo(data));

			CompletableFuture
					.allOf(currentTaskInfo, dailySchedulingFrequencyInfo, TaskStateInfo, schedulingInfo, jobLogInfo)
					.join();

			homeData.setCurrentTaskInfo(currentTaskInfo.get());
			homeData.setDailySchedulingFrequencyInfo(dailySchedulingFrequencyInfo.get());
			homeData.setTaskStateInfo(TaskStateInfo.get());

		} catch (Exception e) {
			LOGGER.error("获取首页数据失败", ExceptionUtils.extractConcurrentException(e));
			return Y9Result.failure("获取首页数据失败");
		}

		return Y9Result.success(homeData, "获取首页数据成功");
	}

	@Override
	public CurrentTaskInfo getCurrentTaskInfo(CurrentTaskQueryInfo currentTaskQueryInfo) {

		CurrentTaskInfo currentTaskInfo = null;
		CompletableFuture<CurrentTaskInfo> createCurrentTaskInfo = createCurrentTaskInfo(currentTaskQueryInfo);
		try {
			currentTaskInfo = createCurrentTaskInfo.get();
		} catch (Exception e) {
			LOGGER.error("获取当前运行任务情况失败", ExceptionUtils.extractConcurrentException(e));
			currentTaskInfo = new CurrentTaskInfo();
		}
		return currentTaskInfo;
	}

	public CompletableFuture<CurrentTaskInfo> createCurrentTaskInfo(CurrentTaskQueryInfo currentTaskQueryInfo) {

		try {
			// 全部任务
			Long startTime = currentTaskQueryInfo.getStartTime();
			Long endTime = currentTaskQueryInfo.getEndTime();
			CompletableFuture<Integer> allTasksCF = CompletableFuture.supplyAsync(
					() -> jobLogService.getExecutedCountByStatusAndTime(
							Arrays.asList(JobLog.SUCCESS, JobLog.ERROR, JobLog.START), startTime, endTime),
					homeDataExecutor);
			// 正在执行
			CompletableFuture<Integer> executingCF = CompletableFuture.supplyAsync(() -> jobLogService
							.getExecutedCountByStatusAndTime(Arrays.asList(JobLog.START), startTime, endTime),
					homeDataExecutor);
			// 今日已经执行
			long[] currentDayTimestamp = DateUtils.getCurrentDayStartAndEndDateTimestamp();
			CompletableFuture<Integer> executedTodayCF = CompletableFuture.supplyAsync(
					() -> jobLogService.getExecutedCountByStatusAndTime(Arrays.asList(JobLog.SUCCESS, JobLog.ERROR),
							currentDayTimestamp[0], currentDayTimestamp[1]),
					homeDataExecutor);
			return CompletableFuture.allOf(allTasksCF, executingCF, executedTodayCF).thenApply(v -> {
				try {
					int allTasks = allTasksCF.get();
					int executing = executingCF.get();
					int executedToday = executedTodayCF.get();

					CurrentTaskInfo info = new CurrentTaskInfo(executing, allTasks, executedToday);
					return info;
				} catch (Exception e) {
					LOGGER.error("获取当前运行任务情况失败", ExceptionUtils.extractConcurrentException(e));
					return new CurrentTaskInfo();
				}
			});
		} catch (Exception e) {
			LOGGER.error("获取当前运行任务情况失败", ExceptionUtils.extractConcurrentException(e));
		}
		return new CompletableFuture<>();
	}

	@Override
	public DailySchedulingFrequencyInfo getDailySchedulingFrequencyInfo(
			DailySchedulingFrequencyQueryInfo dailySchedulingFrequencyQueryInfo) {

		DailySchedulingFrequencyInfo result = new DailySchedulingFrequencyInfo();
		try {
			Long startTime = dailySchedulingFrequencyQueryInfo.getStartTime();
			Long endTime = dailySchedulingFrequencyQueryInfo.getEndTime();
			List<Map<String, Object>> dataList = jobLogService.getExecutedCountGroupByDispatchTime(
					Arrays.asList(JobLog.SUCCESS, JobLog.ERROR, JobLog.START), startTime, endTime);
			Map<String, List<String>> resultMap = new HashMap<>();

			for (Map<String, Object> map : dataList) {
				map.forEach((key, value) -> resultMap.computeIfAbsent(key, k -> new ArrayList<>()).add(value.toString()));
			}
			List<String> dateList = resultMap.get("execute_start_time");
			List<String> frequencyList = resultMap.get("execute_count");
			if (dateList == null || frequencyList == null) {
				dateList = new ArrayList<>();
				frequencyList = new ArrayList<>();
			}
			result.setDateList(dateList);
			result.setFrequencyList(frequencyList);
		} catch (Exception e) {
			LOGGER.error("获取任务执行频率失败情况失败", ExceptionUtils.extractConcurrentException(e));
		}
		return result;
	}

	// 处理查询为空的情况
	@SuppressWarnings("unused")
	private void handleDailySchedulingFrequencyResult(List<String> dateList, List<String> frequencyList, Long startTime,
													  Long endTime) {

		LocalDate startDate = LocalDate.ofEpochDay(startTime / 86400000); // 起始日期
		LocalDate endDate = LocalDate.ofEpochDay(endTime / 86400000); // 结束日期
		for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
			dateList.add(date.toString());
			frequencyList.add("0");
		}

	}
	
	public static <T, R> List<R> flatMapAndCollect(List<T> dataList, Function<T, Collection<R>> mapper) {
		return dataList.stream().flatMap(mapper.andThen(Collection::stream)).collect(Collectors.toList());
	}

	@Override
	public List<Map<String, Object>> getTaskStateInfo(TaskStateQueryInfo taskStateQueryInfo) {

		List<Map<String, Object>> result = null;
		try {
			// 全部任务
			Long startTime = taskStateQueryInfo.getStartTime();
			Long endTime = taskStateQueryInfo.getEndTime();

			Map<String, Integer> data = jobService.getNormalStateTaskNumber(
					Arrays.asList(JobLog.SUCCESS, JobLog.ERROR, JobLog.START), startTime, endTime, Arrays.asList(0, 1));
			Integer activeTaskCount = data.get("active");
			Integer notActiveCount = data.get("notActive");
			result = Stream.of(createDataMap("活跃", activeTaskCount), createDataMap("不活跃", notActiveCount))
					.collect(Collectors.toList());
			data = null;
		} catch (Exception e) {
			LOGGER.error("获取活跃任务占比情况失败", ExceptionUtils.extractConcurrentException(e));
			result = new ArrayList<>();
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
	public SchedulingInfo getSchedulingInfo(SchedulingQueryInfo schedulingQueryInfo) {

		SchedulingInfo result = null;
		try {
			Long startTime = schedulingQueryInfo.getStartTime();
			Long endTime = schedulingQueryInfo.getEndTime();

			if (startTime == null || endTime == null) {
				HomeData.QueryTimeRange lastMonth = QueryTimeRangeCacheUtil.getQueryTimeRangeByName(QueryTimeRangeCacheUtil.LAST_MONTH);
				startTime = lastMonth.getStartTime();
				endTime = lastMonth.getEndTime();

			}
			List<Map<String, Object>> records = jobLogService.getSchedulingInfo(
					Arrays.asList(JobLog.SUCCESS, JobLog.ERROR, JobLog.START), startTime, endTime,
					schedulingQueryInfo.getEnvironment());

			result = convertToSchedulingInfo(records);
			records = null;
		} catch (Exception e) {
			LOGGER.error("获取任务调度情况失败", ExceptionUtils.extractConcurrentException(e));
			result = new SchedulingInfo();
		}
		return result;
	}

	public static SchedulingInfo convertToSchedulingInfo(List<Map<String, Object>> databaseData) {
		Set<String> typeSet = databaseData.stream().map(data -> data.get("status").toString())
				.collect(Collectors.toSet());

		List<String> dateList = databaseData.stream().map(data -> data.get("execute_start_time").toString()).distinct()
				.sorted().collect(Collectors.toList());

		List<Map<String, Object>> taskScheduLingInfo = new ArrayList<>();
		for (String type : typeSet) {
			String typeName = JobLogStatusEnum.fromCode(type).getDescription();
			List<String> countList = new ArrayList<>(Collections.nCopies(dateList.size(), "0"));

			for (Map<String, Object> data : databaseData) {
				if (type.equals(data.get("status").toString())) {
					String date = data.get("execute_start_time").toString();
					int index = dateList.indexOf(date);
					String count = String.valueOf(data.get("execute_count"));
					countList.set(index, count);
				}
			}

			Map<String, Object> taskData = new HashMap<>();
			taskData.put("name", typeName);
			taskData.put("data", countList);
			taskScheduLingInfo.add(taskData);
		}
		SchedulingInfo schedulingInfo = new SchedulingInfo();
		schedulingInfo
				.setTypeList(typeSet.stream().map(type -> JobLogStatusEnum.fromCode(type).getDescription()).collect(Collectors.toList()));
		schedulingInfo.setDateList(dateList);
		schedulingInfo.setTaskScheduLingInfo(taskScheduLingInfo);
		return schedulingInfo;
	}

	@Override
	public JobLogInfo getJobLogInfo(JobLogQueryInfo jobLogQueryInfo) {

		JobLogInfo result = null;
		try {
			Long startTime = jobLogQueryInfo.getStartTime();
			Long endTime = jobLogQueryInfo.getEndTime();

			List<Map<String, Object>> records = jobLogService
					.getLogGroupInfo(Arrays.asList(JobLog.SUCCESS, JobLog.ERROR), startTime, endTime);

			result = convertToJobLogInfo(records);
			records = null;
		} catch (Exception e) {
			result = new JobLogInfo();
			LOGGER.error("获取任务调度情况失败", ExceptionUtils.extractConcurrentException(e));
		}

		return result;
	}

	private JobLogInfo convertToJobLogInfo(List<Map<String, Object>> records) {
		List<LogGroupInfo> logGroupInfos = records.stream().map(record -> {
			long successCount = ((BigDecimal) record.get("success")).longValue();
			long failureCount = ((BigDecimal) record.get("failure")).longValue();
			String executeStartTime = (String) record.get("execute_start_time");
			return new LogGroupInfo(successCount, failureCount, executeStartTime);
		}).collect(Collectors.toList());

		long totalSuccessCount = logGroupInfos.stream().mapToLong(LogGroupInfo::getSuccess).sum();
		long totalFailureCount = logGroupInfos.stream().mapToLong(LogGroupInfo::getFailure).sum();

		return new JobLogInfo(totalSuccessCount, totalFailureCount, logGroupInfos);
	}

}
