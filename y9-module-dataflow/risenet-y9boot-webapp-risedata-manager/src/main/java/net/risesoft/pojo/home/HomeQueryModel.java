package net.risesoft.pojo.home;

import java.time.LocalDate;
import java.time.ZoneOffset;

public class HomeQueryModel {

	private CurrentTaskQueryInfo currentTaskQueryInfo;

	private DailySchedulingFrequencyQueryInfo dailySchedulingFrequencyQueryInfo;

	private TaskStateQueryInfo taskStateQueryInfo;

	private SchedulingQueryInfo schedulingQueryInfo;
	
	private JobLogQueryInfo jobLogQueryInfo;

	public HomeQueryModel() {
		super();
		LocalDate today = LocalDate.now();
//		long dayOfMonthTimeStampStart = today.withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
//		long dayOfMonthTimeStampEnd = today.withDayOfMonth(today.lengthOfMonth()).atTime(23, 59, 59)
//		.toInstant(ZoneOffset.UTC).toEpochMilli();

		long dayOfMonthTimeStampStart = today.minusWeeks(4).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
		long dayOfMonthTimeStampEnd = today.atTime(23, 59, 59).toInstant(ZoneOffset.UTC).toEpochMilli();

		long dayOfWeekTimeStampStart = today.minusDays(today.getDayOfWeek().getValue() - 1).atStartOfDay()
				.toInstant(ZoneOffset.UTC).toEpochMilli();
		long dayOfWeekTimeStampEnd = today.plusDays(7 - today.getDayOfWeek().getValue()).atTime(23, 59, 59)
				.toInstant(ZoneOffset.UTC).toEpochMilli();
		currentTaskQueryInfo = new CurrentTaskQueryInfo(dayOfMonthTimeStampStart, dayOfMonthTimeStampEnd);

		// 默认周
		dailySchedulingFrequencyQueryInfo = new DailySchedulingFrequencyQueryInfo(dayOfWeekTimeStampStart,
				dayOfWeekTimeStampEnd);
		taskStateQueryInfo = new TaskStateQueryInfo(dayOfMonthTimeStampStart, dayOfMonthTimeStampEnd);

		// 默认月
		schedulingQueryInfo = new SchedulingQueryInfo(dayOfMonthTimeStampStart, dayOfMonthTimeStampEnd);
	
		jobLogQueryInfo=new JobLogQueryInfo(dayOfMonthTimeStampStart,dayOfMonthTimeStampEnd);
	}

	public CurrentTaskQueryInfo getCurrentTaskQueryInfo() {
		return currentTaskQueryInfo;
	}

	public void setCurrentTaskQueryInfo(CurrentTaskQueryInfo currentTaskQueryInfo) {
		this.currentTaskQueryInfo = currentTaskQueryInfo;
	}

	public DailySchedulingFrequencyQueryInfo getDailySchedulingFrequencyQueryInfo() {
		return dailySchedulingFrequencyQueryInfo;
	}

	public void setDailySchedulingFrequencyQueryInfo(
			DailySchedulingFrequencyQueryInfo dailySchedulingFrequencyQueryInfo) {
		this.dailySchedulingFrequencyQueryInfo = dailySchedulingFrequencyQueryInfo;
	}

	public TaskStateQueryInfo getTaskStateQueryInfo() {
		return taskStateQueryInfo;
	}

	public void setTaskStateQueryInfo(TaskStateQueryInfo taskStateQueryInfo) {
		this.taskStateQueryInfo = taskStateQueryInfo;
	}

	public SchedulingQueryInfo getSchedulingQueryInfo() {
		return schedulingQueryInfo;
	}

	public void setSchedulingQueryInfo(SchedulingQueryInfo schedulingQueryInfo) {
		this.schedulingQueryInfo = schedulingQueryInfo;
	}

	
	
	public JobLogQueryInfo getJobLogQueryInfo() {
		return jobLogQueryInfo;
	}

	public void setJobLogQueryInfo(JobLogQueryInfo jobLogQueryInfo) {
		this.jobLogQueryInfo = jobLogQueryInfo;
	}



	public static class CurrentTaskQueryInfo {
		private Long startTime;
		private Long endTime;

		public Long getStartTime() {
			return startTime;
		}

		public void setStartTime(Long startTime) {
			this.startTime = startTime;
		}

		public Long getEndTime() {
			return endTime;
		}

		public void setEndTime(Long endTime) {
			this.endTime = endTime;
		}

		public CurrentTaskQueryInfo(Long startTime, Long endTime) {
			super();
			this.startTime = startTime;
			this.endTime = endTime;
		}

		public CurrentTaskQueryInfo() {
			super();
		}

	}

	public static class DailySchedulingFrequencyQueryInfo {
		private Long startTime;
		private Long endTime;

		public Long getStartTime() {
			return startTime;
		}

		public void setStartTime(Long startTime) {
			this.startTime = startTime;
		}

		public Long getEndTime() {
			return endTime;
		}

		public void setEndTime(Long endTime) {
			this.endTime = endTime;
		}

		public DailySchedulingFrequencyQueryInfo(Long startTime, Long endTime) {
			super();
			this.startTime = startTime;
			this.endTime = endTime;
		}

		public DailySchedulingFrequencyQueryInfo() {
			super();
		}

	}

	public static class TaskStateQueryInfo {
		private Long startTime;
		private Long endTime;

		public Long getStartTime() {
			return startTime;
		}

		public void setStartTime(Long startTime) {
			this.startTime = startTime;
		}

		public Long getEndTime() {
			return endTime;
		}

		public void setEndTime(Long endTime) {
			this.endTime = endTime;
		}

		public TaskStateQueryInfo(Long startTime, Long endTime) {
			super();
			this.startTime = startTime;
			this.endTime = endTime;
		}

		public TaskStateQueryInfo() {
			super();
		}

	}

	// 调度信息查询
	public static class SchedulingQueryInfo {
		private Long startTime;
		private Long endTime;
		private String environment;

		public Long getStartTime() {
			return startTime;
		}

		public void setStartTime(Long startTime) {
			this.startTime = startTime;
		}

		public Long getEndTime() {
			return endTime;
		}

		public void setEndTime(Long endTime) {
			this.endTime = endTime;
		}

		public String getEnvironment() {
			return environment;
		}

		public void setEnvironment(String environment) {
			this.environment = environment;
		}

		public SchedulingQueryInfo(Long startTime, Long endTime) {
			super();
			this.startTime = startTime;
			this.endTime = endTime;
		}

		public SchedulingQueryInfo(Long startTime, Long endTime, String environment) {
			super();
			this.startTime = startTime;
			this.endTime = endTime;
			this.environment = environment;
		}

		public SchedulingQueryInfo() {
			super();
		}

	}

	class BaseQueryModel {
		private Long startTime;
		private Long endTime;

		public Long getStartTime() {
			return startTime;
		}

		public void setStartTime(Long startTime) {
			this.startTime = startTime;
		}

		public Long getEndTime() {
			return endTime;
		}

		public void setEndTime(Long endTime) {
			this.endTime = endTime;
		}

		public BaseQueryModel() {
			super();
		}

	}

	// 调度信息查询
	public static class JobLogQueryInfo {
		private Long startTime;
		private Long endTime;

		public Long getStartTime() {
			return startTime;
		}

		public void setStartTime(Long startTime) {
			this.startTime = startTime;
		}

		public Long getEndTime() {
			return endTime;
		}

		public void setEndTime(Long endTime) {
			this.endTime = endTime;
		}

		public JobLogQueryInfo(Long startTime, Long endTime) {
			super();
			this.startTime = startTime;
			this.endTime = endTime;
		}

		public JobLogQueryInfo() {
			super();
		}

	}

}
