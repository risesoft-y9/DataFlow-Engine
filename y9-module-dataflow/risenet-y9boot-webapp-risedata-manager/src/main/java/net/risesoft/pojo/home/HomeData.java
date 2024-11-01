package net.risesoft.pojo.home;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
import net.risesoft.security.model.Environment;
import net.risesoft.util.home.QueryTimeRangeCacheUtil;

public class HomeData {

    public CurrentTaskInfo getCurrentTaskInfo() {
        return currentTaskInfo;
    }

    public List<QueryTimeRange> getTimeRanges() {
        return timeRanges;
    }

    public void setTimeRanges(List<QueryTimeRange> timeRanges) {
        this.timeRanges = timeRanges;
    }

    public DailySchedulingFrequencyInfo getDailySchedulingFrequencyInfo() {
        return dailySchedulingFrequencyInfo;
    }

    public void setDailySchedulingFrequencyInfo(DailySchedulingFrequencyInfo dailySchedulingFrequencyInfo) {
        this.dailySchedulingFrequencyInfo = dailySchedulingFrequencyInfo;
    }

    public SchedulingInfo getSchedulingInfo() {
        return schedulingInfo;
    }

    public void setSchedulingInfo(SchedulingInfo schedulingInfo) {
        this.schedulingInfo = schedulingInfo;
    }

    private List<QueryTimeRange> timeRanges;

    public JobLogInfo getJobLogInfo() {
        return jobLogInfo;
    }

    public void setJobLogInfo(JobLogInfo jobLogInfo) {
        this.jobLogInfo = jobLogInfo;
    }

    public HomeData() {
        super();
        this.timeRanges = QueryTimeRangeCacheUtil.getQueryTimeRangeList();

    }

    public HomeData setCurrentTaskInfo(CurrentTaskInfo currentTaskInfo) {
        this.currentTaskInfo = currentTaskInfo;
        return this;
    }

    private CurrentTaskInfo currentTaskInfo;

    // 当前运行任务情况
    @Getter
    @Setter
    public static class CurrentTaskInfo {
        Integer executing;// 正在执行
        Integer allTasks;// 全部任务
        Integer executedToday;// 今日已经执行

        public CurrentTaskInfo() {
            super();
        }

        public CurrentTaskInfo(Integer executing, Integer allTasks, Integer executedToday) {
            super();
            this.executing = executing;
            this.allTasks = allTasks;
            this.executedToday = executedToday;
        }
    }

    private DailySchedulingFrequencyInfo dailySchedulingFrequencyInfo;

    // 每日调度次数情况
    @Getter
    @Setter
    public static class DailySchedulingFrequencyInfo {
        List<String> dateList = null;
        List<String> frequencyList = null;
    }

    // 任务状态信息
    @Getter
    @Setter
    private List<Map<String, Object>> TaskStateInfo;

    // 调度情况
    private SchedulingInfo schedulingInfo;

    @Getter
    @Setter
    public static class SchedulingInfo {
        private List<String> typeList;
        private List<String> dateList;
        List<Map<String, Object>> taskScheduLingInfo;
    }

    public static class StatusInfo {
        private String name;
        private List<String> data;

        public StatusInfo(String name, List<String> data) {
            this.name = name;
            this.data = data;
        }

        public String getName() {
            return name;
        }

        public List<String> getData() {
            return data;
        }
    }

    // 环境
    @Getter
    @Setter
    private List<Environment> allEnvironments;

    // 查询时间范围
    @Getter
    @Setter
    public static class QueryTimeRange {
        private String name;
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long startTime;
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long endTime;

        public QueryTimeRange(String name, Long startTime, Long endTime) {
            this.name = name;
            this.startTime = startTime;
            this.endTime = endTime;
        }

    }

    // 日志
    @Getter
    @Setter
    public static class JobLogInfo {
        private long successCount;
        private long failureCount;
        private List<LogGroupInfo> logGroupInfo;

        public JobLogInfo() {
            super();
        }

        public JobLogInfo(long successCount, long failureCount, List<LogGroupInfo> logGroupInfo) {
            super();
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.logGroupInfo = logGroupInfo;
        }

    }

    @Getter
    @Setter
    public static class LogGroupInfo {
        private Long success;
        private Long failure;
        private String executeStartTime;

        public LogGroupInfo(Long success, Long failure, String executeStartTime) {
            super();
            this.success = success;
            this.failure = failure;
            this.executeStartTime = executeStartTime;
        }

    }

    private JobLogInfo jobLogInfo;

}
