package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataApiOnlineService;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataApiOnlineEntity;
import net.risesoft.y9public.entity.DataApiOnlineInfoEntity;

@Validated
@RestController
@RequestMapping(value = "/api/rest/apionline", produces = "application/json")
@RequiredArgsConstructor
public class ApiOnlineController {

	private final DataApiOnlineService dataApiOnlineService;
	
	/**
	 * 保存接口信息
	 * @param entity
	 * @return
	 */
	@PostMapping(value = "/saveData")
	public Y9Result<DataApiOnlineEntity> saveData(@RequestBody String json) {
		Map<String, Object> map = Y9JsonUtil.readHashMap(json);
		
		DataApiOnlineEntity entity = new DataApiOnlineEntity();
		entity.setId((String)map.get("id"));
		entity.setName((String)map.get("name"));
		entity.setParentId((String)map.get("parentId"));
		entity.setType((String)map.get("type"));
		
		if(map.get("type").equals("folder")) {
			return dataApiOnlineService.saveData(entity, null);
		}
		DataApiOnlineInfoEntity infoEntity = new DataApiOnlineInfoEntity();
		infoEntity.setId(entity.getId());
		infoEntity.setFormData(Y9JsonUtil.writeValueAsString(map.get("ApiForm")));
		return dataApiOnlineService.saveData(entity, infoEntity);
	}
	
	/**
	 * 删除接口信息
	 * @param id
	 * @return
	 */
	@PostMapping(value = "/deleteData")
	public Y9Result<List<String>> deleteData(@RequestParam String id) {
		return dataApiOnlineService.deleteData(id);
	}
	
	/**
	 * 获取接口树
	 * @param id
	 * @return
	 */
	@GetMapping(value = "/getTree")
	public Y9Result<List<Map<String, Object>>> getTree(String id) {
		return Y9Result.success(dataApiOnlineService.getTree(id));
	}
	
}
