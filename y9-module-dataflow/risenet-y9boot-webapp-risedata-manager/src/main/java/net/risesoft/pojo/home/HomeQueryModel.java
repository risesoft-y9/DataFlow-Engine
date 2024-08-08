package net.risesoft.pojo.home;

import net.risesoft.util.home.QueryTimeRangeCacheUtil;

import java.time.LocalDate;

public class HomeQueryModel {

    private CurrentTaskQueryInfo currentTaskQueryInfo;

    private DailySchedulingFrequencyQueryInfo dailySchedulingFrequencyQueryInfo;

    private TaskStateQueryInfo taskStateQueryInfo;

    private SchedulingQueryInfo schedulingQueryInfo;

    private JobLogQueryInfo jobLogQueryInfo;

    public HomeQueryModel() {
        super();
        HomeData.QueryTimeRange lastWeek = QueryTimeRangeCacheUtil.getQueryTimeRangeByName(QueryTimeRangeCacheUtil.LAST_WEEK);
        HomeData.QueryTimeRange lastMonth = QueryTimeRangeCacheUtil.getQueryTimeRangeByName(QueryTimeRangeCacheUtil.LAST_MONTH);
        Long lastMonthStartTime = lastMonth.getStartTime();
        Long lastMonthEndTime = lastMonth.getEndTime();

        Long lastWeekStartTime = lastWeek.getStartTime();
        long lastWeekEndTime = lastWeek.getEndTime();

        // 默认周
        jobLogQueryInfo = new JobLogQueryInfo(lastWeekStartTime, lastWeekEndTime);
        dailySchedulingFrequencyQueryInfo = new DailySchedulingFrequencyQueryInfo(lastWeekStartTime,
                lastWeekEndTime);//每日调度

        // 默认月
        currentTaskQueryInfo = new CurrentTaskQueryInfo(lastMonthStartTime, lastMonthEndTime);
        taskStateQueryInfo = new TaskStateQueryInfo(lastMonthStartTime, lastMonthEndTime);
        schedulingQueryInfo = new SchedulingQueryInfo(lastMonthStartTime, lastMonthEndTime);

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
