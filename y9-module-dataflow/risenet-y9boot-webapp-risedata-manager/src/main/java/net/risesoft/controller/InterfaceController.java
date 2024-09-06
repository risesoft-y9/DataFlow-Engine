package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.model.RequestModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataInterfaceService;
import net.risesoft.util.ApiTest;
import net.risesoft.y9public.entity.DataInterfaceEntity;
import net.risesoft.y9public.entity.DataInterfaceParamsEntity;

@Validated
@RestController
@RequestMapping(value = "/api/rest/interface", produces = "application/json")
@RequiredArgsConstructor
public class InterfaceController {

	private final DataInterfaceService dataInterfaceService;
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "分页获取接口信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/searchPage")
	public Y9Page<DataInterfaceEntity> searchPage(String search, String requestType, Integer dataType, Integer page, Integer size) {
		Page<DataInterfaceEntity> pageList = dataInterfaceService.searchPage(search, requestType, dataType, page, size);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(), "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存接口信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveData")
	public Y9Result<DataInterfaceEntity> saveData(DataInterfaceEntity entity) {
		return dataInterfaceService.saveData(entity);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据id获取接口信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getDataById")
	public Y9Result<DataInterfaceEntity> getDataById(@RequestParam String id) {
		return Y9Result.success(dataInterfaceService.getById(id), "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除接口数据", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteData")
	public Y9Result<String> deleteData(@RequestParam String id) {
		return dataInterfaceService.deleteData(id);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取接口和参数信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/findParamsList")
	public Y9Result<Map<String, Object>> findParamsList(@RequestParam String parentId, Integer dataType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("interface", dataInterfaceService.getById(parentId));
		map.put("params", dataInterfaceService.findByParentIdAndDataType(parentId, dataType));
		return Y9Result.success(map, "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存接口参数信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveParams")
	public Y9Result<DataInterfaceParamsEntity> saveParams(DataInterfaceParamsEntity entity) {
		return dataInterfaceService.saveData(entity);
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除接口参数数据", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteParams")
	public Y9Result<String> deleteParams(@RequestParam String id) {
		return dataInterfaceService.deleteParam(id);
	}
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存接口返回参数数据", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveResponseParams")
	public Y9Result<String> saveResponseParams(@RequestBody String data) {
		return dataInterfaceService.saveResponseParams(data);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取接口返回字段信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/getApiField")
	public Y9Result<List<Map<String, Object>>> getApiField(@RequestParam String apiId) {
		List<Map<String, Object>> listMap = new ArrayList<>();
		DataInterfaceEntity dataInterfaceEntity = dataInterfaceService.getById(apiId);
		List<DataInterfaceParamsEntity> paramList = null;
		if(dataInterfaceEntity.getPattern() == 0) {
			// 获取接口取返回参数字段
			paramList = dataInterfaceService.findByParentIdAndDataType(apiId, 1);
		}else {
			// 保存接口取请求参数字段
			paramList = dataInterfaceService.findByParentIdAndDataType(apiId, 0);
		}
		for(DataInterfaceParamsEntity param : paramList) {
			if(param.getReqType().equals("Headers")) {
				continue;
			}
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", param.getId());
			map.put("name", param.getParamName());
			map.put("cname", param.getRemark());
			map.put("fieldType", param.getParamType());
			listMap.add(map);
		}
        return Y9Result.success(listMap, "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.CHECK, operationName = "接口测试", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/apiTest")
	public Y9Result<String> apiTest(@RequestBody RequestModel requestModel) {
		return Y9Result.success(ApiTest.sendApi(requestModel), "请求成功");
	}
	
}
