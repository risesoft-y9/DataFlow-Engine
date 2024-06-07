package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataMappingService;
import net.risesoft.y9public.entity.DataMappingArgsEntity;
import net.risesoft.y9public.entity.DataMappingEntity;

@Validated
@RestController
@RequestMapping(value = "/api/rest/mapping", produces = "application/json")
@RequiredArgsConstructor
public class MappingController {

	private final DataMappingService dataMappingService;
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "分页获取配置映射数据", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/getMappingPage")
	public Y9Page<DataMappingEntity> getFieldAll(String typeName, String className, Integer page, Integer size) {
		Page<DataMappingEntity> pageList = dataMappingService.getDataPage(typeName, className, page, size);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(), "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存配置映射信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveData")
	public Y9Result<DataMappingEntity> saveData(DataMappingEntity entity) {
		return dataMappingService.saveData(entity);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据id获取配置映射信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getDataById")
	public Y9Result<DataMappingEntity> getDataById(String id) {
		return Y9Result.success(dataMappingService.getById(id), "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除配置映射数据", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteData")
	public Y9Result<String> deleteData(String id) {
		return dataMappingService.deleteData(id);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "分页获取配置映射参数数据", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/getArgsPage")
	public Y9Page<DataMappingArgsEntity> getArgsPage(String mappingId, Integer page, Integer size) {
		Page<DataMappingArgsEntity> pageList = dataMappingService.getArgsDataPage(mappingId, page, size);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(), "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存配置映射参数信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveArgsData")
	public Y9Result<DataMappingArgsEntity> saveArgsData(DataMappingArgsEntity entity) {
		return dataMappingService.saveArgsData(entity);
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除配置映射参数数据", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteArgs")
	public Y9Result<String> deleteArgs(String id) {
		return dataMappingService.deleteArgs(id);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据类别获取页面信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/findByTypeName")
	public Y9Result<List<Map<String, Object>>> findByTypeName(String typeName) {
		return Y9Result.success(dataMappingService.findByTypeName(typeName), "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据映射表id获取参数列表", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/findByMappingId")
	public Y9Result<List<DataMappingArgsEntity>> findByMappingId(String mappingId) {
		return Y9Result.success(dataMappingService.findByMappingId(mappingId), "获取成功");
	}
}
