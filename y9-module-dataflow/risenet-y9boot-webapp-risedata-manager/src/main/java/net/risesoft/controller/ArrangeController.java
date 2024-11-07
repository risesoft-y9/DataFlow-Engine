package net.risesoft.controller;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataArrangeService;
import net.risesoft.y9public.entity.DataArrangeEntity;

@Validated
@RestController
@RequestMapping(value = "/api/rest/arrange", produces = "application/json")
@RequiredArgsConstructor
public class ArrangeController {

	private final DataArrangeService dataArrangeService;
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "分页获取任务编排数据", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/searchPage")
	public Y9Page<DataArrangeEntity> searchPage(String name, Integer pattern, Integer page, Integer size) {
		Page<DataArrangeEntity> pageList = dataArrangeService.searchPage(name, pattern, page, size);
		pageList.stream().map((item) -> {
			item.setXmlData("");
            return item;
        }).collect(Collectors.toList());
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(), "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存任务编排信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveData")
	public Y9Result<DataArrangeEntity> saveData(DataArrangeEntity entity) {
		return dataArrangeService.saveData(entity);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据id获取任务编排信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getDataById")
	public Y9Result<DataArrangeEntity> getDataById(@RequestParam String id) {
		return Y9Result.success(dataArrangeService.getById(id), "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除任务编排数据", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteData")
	public Y9Result<String> deleteData(@RequestParam String id) {
		return dataArrangeService.deleteData(id);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据id获取任务编排流程信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getXmlById")
	public Y9Result<String> getXmlById(@RequestParam String id) {
		return Y9Result.success(dataArrangeService.getById(id).getXmlData(), "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存流程信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveXml")
	public Y9Result<String> saveXml(String id, String xmlData, String parseData) {
		return dataArrangeService.saveXml(id, xmlData, parseData);
	}
	
	@RiseLog(operationType = OperationTypeEnum.SEND, operationName = "执行编排任务", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/executeProcess")
	public Y9Result<String> executeProcess(@RequestParam String id) {
		return dataArrangeService.executeProcess(id);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据id获取任务编排日志信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getLogList")
	public Y9Page<Map<String, Object>> getLogList(@RequestParam String id, int page, int size) {
		return dataArrangeService.getLogList(id, page, size);
	}
	
}
