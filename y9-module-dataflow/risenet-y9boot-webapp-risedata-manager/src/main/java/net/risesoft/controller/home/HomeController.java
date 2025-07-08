package net.risesoft.controller.home;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.HomeDataService;

@RestController
@RequestMapping(value = "/api/rest/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeDataService homeDataService;

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取首页当前任务运行情况数据", logLevel = LogLevelEnum.RSLOG, enable = false)
    @GetMapping(value = "/getHomeData")
    public Y9Result<Map<String, Object>> getHomeData(String type) {
        return homeDataService.getCurrentTaskInfo(type);
    }

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取调度情况数据", logLevel = LogLevelEnum.RSLOG, enable = false)
    @GetMapping(value = "/getSchedulingInfo")
    public Y9Result<Map<String, Object>> getSchedulingInfo(String type) {
        return Y9Result.success(homeDataService.getSchedulingInfo(type), "获取调度情况数据");
    }

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取每日调度次数", logLevel = LogLevelEnum.RSLOG, enable = false)
    @GetMapping(value = "/getDailyInfo")
    public Y9Result<Map<String, Object>> getDailyInfo(String type) {
        return homeDataService.getDailySchedulingFrequencyInfo(type);
    }

    @RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取调度任务状态比例", logLevel = LogLevelEnum.RSLOG, enable = false)
    @GetMapping(value = "/getTaskStateInfo")
    public Y9Result<List<Map<String, Object>>> getTaskStateInfo(String type) {
        return Y9Result.success(homeDataService.getTaskStateInfo(type), "获取任务状态比例");
    }
}
