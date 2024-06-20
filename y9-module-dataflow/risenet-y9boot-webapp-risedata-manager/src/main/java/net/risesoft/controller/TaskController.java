package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.persistence.job.JobService;
import net.risesoft.api.security.ConcurrentSecurity;
import net.risesoft.api.security.SecurityManager;
import net.risesoft.log.LogLevelEnum;
import net.risesoft.log.OperationTypeEnum;
import net.risesoft.log.annotation.RiseLog;
import net.risesoft.pojo.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataBusinessService;
import net.risesoft.service.DataTaskService;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataTaskEntity;

@Validated
@RestController
@RequestMapping(value = "/api/rest/task", produces = "application/json")
@RequiredArgsConstructor
public class TaskController {

	private final DataTaskService dataTaskService;
	private final DataBusinessService dataBusinessService;
	private final JobService jobService;
	private final SecurityManager securityManager;
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "分页获取任务列表", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping("/findPage")
	public Y9Page<Map<String, Object>> findPage(String jobId, String name, String businessId, Integer page, Integer size) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		List<String> ids = new ArrayList<String>();
		if(StringUtils.isNotBlank(jobId)) {
			ids = jobService.findArgsById(jobId);
		}
		// 获取权限
		ConcurrentSecurity security = securityManager.getConcurrentSecurity();
		List<String> bIds = security.getJobTypes();
		if(StringUtils.isNotBlank(businessId)) {
			if(bIds.size() == 0 || bIds.contains(businessId)) {
				bIds = new ArrayList<String>();
				bIds.add(businessId);
			}
		}
		Page<DataTaskEntity> pageList = dataTaskService.findPage(ids, name, bIds, page, size);
		for(DataTaskEntity task : pageList) {
			Map<String, Object> row = new HashMap<String, Object>();
			row.put("id", task.getId());
			row.put("name", task.getName());
			row.put("description", task.getDescription());
			row.put("businessId", task.getBusinessId());
			row.put("business", dataBusinessService.getById(task.getBusinessId()).getName());
			row.put("createTime", task.getCreateTime());
			row.put("user", "admin");
			int count = jobService.findCountJobByArgs(task.getId());
			if(count == 0) {
				row.put("status", "未设置调度");
			}else {
				row.put("status", "已设调度数：" + count);
			}
			listMap.add(row);
		}
		return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), listMap, "获取数据成功");
    }
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存任务基本信息", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/saveTask")
	public Y9Result<DataTaskEntity> saveTask(DataTaskEntity entity) {
		return dataTaskService.saveData(entity);
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "根据id获取修改任务信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getDataById")
	public Y9Result<TaskModel> getDataById(@RequestParam String id) {
		return Y9Result.success(dataTaskService.getById(id), "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.DELETE, operationName = "删除任务配置", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/deleteData")
	public Y9Result<String> deleteData(@RequestParam String id) {
		dataTaskService.deleteData(id);
		return Y9Result.successMsg("删除成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.ADD, operationName = "保存任务配置", logLevel = LogLevelEnum.RSLOG)
	@PostMapping(value = "/save")
	public Y9Result<DataTaskEntity> save(@RequestBody String json) {
		return dataTaskService.save(Y9JsonUtil.readValue(json, TaskModel.class));
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取任务详情信息", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/getTaskDetails")
	public Y9Result<Map<String, Object>> getTaskDetails(@RequestParam String id) {
		return Y9Result.success(dataTaskService.getTaskDetails(id), "获取成功");
	}
	
	@RiseLog(operationType = OperationTypeEnum.BROWSE, operationName = "获取任务列表", logLevel = LogLevelEnum.RSLOG, enable = false)
	@GetMapping(value = "/findAll")
	public Y9Result<List<DataTaskEntity>> findAll(String businessId) {
		return Y9Result.success(dataTaskService.findAll(businessId), "获取成功");
	}
}
