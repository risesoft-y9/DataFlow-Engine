package net.risesoft.controller;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.dto.DataWatermarkDTO;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataWatermarkService;
import net.risesoft.y9public.entity.DataWatermarkEntity;

@Validated
@RestController
@RequestMapping(value = "/api/rest/watermark", produces = "application/json")
@RequiredArgsConstructor
public class WatermarkController {

	private final DataWatermarkService dataWatermarkService;
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "分页获取水印信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/searchPage")
	public Y9Page<DataWatermarkEntity> searchPage(String id, Integer page, Integer size) {
		if(StringUtils.isNotBlank(id)) {
			DataWatermarkEntity watermarkEntity = dataWatermarkService.getById(id);
			if(watermarkEntity == null) {
				return Y9Page.success(page, 0, 0, null, "获取数据成功");
			} else {
				return Y9Page.success(page, 1, 1, Arrays.asList(watermarkEntity), "获取数据成功");
			}
		}
		Page<DataWatermarkEntity> pageList = dataWatermarkService.searchPage(page, size);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(), "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存水印信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveData")
	public Y9Result<DataWatermarkEntity> saveData(DataWatermarkDTO watermarkDTO) {
		return dataWatermarkService.saveData(watermarkDTO);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据id获取水印信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getDataById")
	public Y9Result<DataWatermarkEntity> getDataById(@RequestParam String id) {
		return Y9Result.success(dataWatermarkService.getById(id), "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除水印数据", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteData")
	public Y9Result<String> deleteData(@RequestParam String id) {
		return dataWatermarkService.deleteData(id);
	}
	
}
