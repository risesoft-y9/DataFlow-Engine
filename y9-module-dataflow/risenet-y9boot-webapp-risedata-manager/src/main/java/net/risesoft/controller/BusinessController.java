package net.risesoft.controller;

import java.util.List;

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
import net.risesoft.service.DataBusinessService;
import net.risesoft.y9public.entity.DataBusinessEntity;

@Validated
@RestController
@RequestMapping(value = "/api/rest/business", produces = "application/json")
@RequiredArgsConstructor
public class BusinessController {

	private final DataBusinessService dataBusinessService;
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "分页获取业务分类", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/getAll")
	public Y9Page<DataBusinessEntity> getAll(String name, String parentId, Integer page, Integer size) {
		Page<DataBusinessEntity> pageList = dataBusinessService.findByNamePage(name, parentId, page, size);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(), "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取业务分类列表", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/findAll")
	public Y9Result<List<DataBusinessEntity>> findAll() {
        return Y9Result.success(dataBusinessService.findAll(), "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存业务分类信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveData")
	public Y9Result<DataBusinessEntity> saveData(DataBusinessEntity entity) {
		return dataBusinessService.saveData(entity);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据id获取业务分类信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getDataById")
	public Y9Result<DataBusinessEntity> getDataById(@RequestParam String id) {
		return Y9Result.success(dataBusinessService.getById(id), "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除业务分类数据", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteData")
	public Y9Result<String> deleteData(@RequestParam String id) {
		return dataBusinessService.deleteData(id);
	}
	
}
