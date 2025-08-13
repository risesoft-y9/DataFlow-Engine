package net.risesoft.controller.home;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.pojo.home.HomeData;
import net.risesoft.pojo.home.HomeData.CurrentTaskInfo;
import net.risesoft.pojo.home.HomeData.DailySchedulingFrequencyInfo;
import net.risesoft.pojo.home.HomeData.JobLogInfo;
import net.risesoft.pojo.home.HomeData.SchedulingInfo;
import net.risesoft.pojo.home.HomeQueryModel.CurrentTaskQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.DailySchedulingFrequencyQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.SchedulingQueryInfo;
import net.risesoft.pojo.home.HomeQueryModel.TaskStateQueryInfo;
import net.risesoft.security.ConcurrentSecurity;
import net.risesoft.security.SecurityManager;
import net.risesoft.pojo.home.HomeQueryModel.JobLogQueryInfo;
import net.risesoft.service.HomeDataService;

@RestController
@RequestMapping(value = "/api/rest/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeDataService homeDataService;
    private final SecurityManager securityManager;

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "异步获取首页数据", logLevel = LogLevelEnum.RSLOG, enable = false)
    @GetMapping(value = "/getHomeData")
    public Y9Result<HomeData> getHomeData() {
        return homeDataService.getHomeData();
    }

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "同步获取首页数据", logLevel = LogLevelEnum.RSLOG, enable = false)
    @GetMapping(value = "/getHomeDataSync")
    public Y9Result<HomeData> getHomeDataSync() {
        return homeDataService.getHomeDataSync();
    }

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取调度情况数据", logLevel = LogLevelEnum.RSLOG, enable = false)
    @PostMapping(value = "/getSchedulingInfo")
    public Y9Result<SchedulingInfo> getSchedulingInfo(@RequestBody SchedulingQueryInfo schedulingQueryInfo) {
    	ConcurrentSecurity security = securityManager.getConcurrentSecurity();
        return Y9Result.success(homeDataService.getSchedulingInfo(schedulingQueryInfo, security.getJobTypes()), "获取调度情况数据");
    }

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取每日调度数据", logLevel = LogLevelEnum.RSLOG, enable = false)
    @PostMapping(value = "/getDailySchedulingFrequencyInfo")
    public Y9Result<DailySchedulingFrequencyInfo> getSchedulingInfo(
            @RequestBody DailySchedulingFrequencyQueryInfo dailySchedulingFrequencyQueryInfo) {
    	ConcurrentSecurity security = securityManager.getConcurrentSecurity();
        return Y9Result.success(homeDataService.getDailySchedulingFrequencyInfo(dailySchedulingFrequencyQueryInfo, security.getJobTypes()),
                "获取每日调度数据");
    }

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取当前任务执行情况", logLevel = LogLevelEnum.RSLOG, enable = false)
    @PostMapping(value = "/getCurrentTaskInfo")
    public Y9Result<CurrentTaskInfo> getCurrentTaskInfo(@RequestBody CurrentTaskQueryInfo currentTaskQueryInfo) {
        return Y9Result.success(homeDataService.getCurrentTaskInfo(currentTaskQueryInfo), "获取当前任务执行情况");
    }

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取任务状态比例", logLevel = LogLevelEnum.RSLOG, enable = false)
    @PostMapping(value = "/getTaskStateInfo")
    public Y9Result<List<Map<String, Object>>> getTaskStateInfo(@RequestBody TaskStateQueryInfo taskStateQueryInfo) {
        return Y9Result.success(homeDataService.getTaskStateInfo(taskStateQueryInfo), "获取任务状态比例");
    }

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取日志信息", logLevel = LogLevelEnum.RSLOG, enable = false)
    @PostMapping(value = "/getJobLogInfo")
    public Y9Result<JobLogInfo> getJobLogInfo(@RequestBody JobLogQueryInfo jobLogQueryInfo) {
    	ConcurrentSecurity security = securityManager.getConcurrentSecurity();
        return Y9Result.success(homeDataService.getJobLogInfo(jobLogQueryInfo, security.getJobTypes()), "获取日志信息成功");
    }
}
