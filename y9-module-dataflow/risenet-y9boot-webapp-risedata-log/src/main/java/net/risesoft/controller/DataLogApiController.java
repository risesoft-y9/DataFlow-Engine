package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataCacheService;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataLogEntity;
import net.risesoft.y9public.repository.DataLogRepository;

@Validated
@RestController
@RequestMapping(value = "/services/rest", produces = "application/json")
@RequiredArgsConstructor
public class DataLogApiController {

	private final DataLogRepository dataLogRepository;
	private final DataCacheService dataCacheService;

	@PostMapping(value = "/saveLogData")
	public Y9Result<String> saveLogData(@RequestBody String json) {
		try {
			List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(json);
			for(Map<String, Object> map : listMap) {
				DataLogEntity dataLogEntity = new DataLogEntity();
				dataLogEntity.setId(Y9IdGenerator.genId());
				dataLogEntity.setJobId((String)map.get("jobId"));
				dataLogEntity.setMsg((String)map.get("msg"));
				dataLogEntity.setDate((long)map.get("date"));
				dataLogEntity.setRecord(Y9JsonUtil.writeValueAsString(map.get("record")));
				dataLogRepository.save(dataLogEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("保存失败：" + e.getMessage());
		}
		return Y9Result.successMsg("保存成功");
	}
	
	@SuppressWarnings("unchecked")
	@PostMapping(value = "/cache")
	public Y9Result<?> cache(@RequestBody String json) {
		try {
			Map<String, Object> map = Y9JsonUtil.readHashMap(json);
			String operation = (String) map.get("operation");// 操作类型
			Map<String, Object> parameter = (Map<String, Object>) map.get("parameter");// 数据
			String key = (String) parameter.get("key");
			if(operation.equals("get")) {
				return dataCacheService.get(key);
			} else if(operation.equals("put")) {
				return dataCacheService.put(key, parameter.get("value"));
			} else if(operation.equals("remove")) {
				return dataCacheService.remove(key);
			} else if(operation.equals("push")) {
				return dataCacheService.push(key, parameter.get("value"));
			} else if(operation.equals("poll")) {
				return dataCacheService.poll(key, Integer.valueOf(parameter.get("size").toString()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Y9Result.success("");
	}

}
