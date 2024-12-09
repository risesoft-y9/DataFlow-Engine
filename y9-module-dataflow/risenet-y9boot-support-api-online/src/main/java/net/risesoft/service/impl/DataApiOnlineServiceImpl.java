package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataApiOnlineService;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataApiOnlineEntity;
import net.risesoft.y9public.entity.DataApiOnlineInfoEntity;
import net.risesoft.y9public.repository.DataApiOnlineInfoRepository;
import net.risesoft.y9public.repository.DataApiOnlineRepository;

@Service(value = "dataApiOnlineService")
@RequiredArgsConstructor
public class DataApiOnlineServiceImpl implements DataApiOnlineService {
	
	private final DataApiOnlineRepository dataApiOnlineRepository;
	private final DataApiOnlineInfoRepository dataApiOnlineInfoRepository;
	
	@Override
	@Transactional(readOnly = false)
	public Y9Result<List<String>> deleteData(String id) {
		List<String> removeIds = new ArrayList<String>();
		try {
			DataApiOnlineEntity dataApiOnlineEntity = dataApiOnlineRepository.findById(id).orElse(null);
			if(dataApiOnlineEntity == null) {
				return Y9Result.failure("删除失败，主体信息不存在");
			}else {
				if(dataApiOnlineEntity.getType().equals("folder")) {
					delete(id, removeIds);
				}else {
					dataApiOnlineInfoRepository.deleteById(id);
				}
				dataApiOnlineRepository.deleteById(id);
				removeIds.add(id);
			}
		} catch (Exception e) {
			return Y9Result.failure("删除失败，" + e.getMessage());
		}
		return Y9Result.success(removeIds);
	}
	
	private void delete(String parentId, List<String> removeIds) {
		List<String> ids = dataApiOnlineRepository.findByParentId(parentId);
		for(String id : ids) {
			DataApiOnlineEntity dataApiOnlineEntity = dataApiOnlineRepository.findById(id).orElse(null);
			if(dataApiOnlineEntity.getType().equals("folder")) {
				delete(dataApiOnlineEntity.getId(), removeIds);
			}else {
				dataApiOnlineInfoRepository.deleteById(id);
			}
			dataApiOnlineRepository.deleteById(id);
			removeIds.add(id);
		}
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataApiOnlineEntity> saveData(DataApiOnlineEntity entity, DataApiOnlineInfoEntity infoEntity) {
		if (StringUtils.isBlank(entity.getId())) {
			entity.setId(Y9IdGenerator.genId());
		}
		if(infoEntity != null) {
			infoEntity.setId(entity.getId());
			dataApiOnlineInfoRepository.save(infoEntity);
		}
		return Y9Result.success(dataApiOnlineRepository.save(entity), "保存成功");
	}

	@Override
	public List<Map<String, Object>> getTree(String id) {
		if(StringUtils.isBlank(id)) {
			id = "0";
		}
		return getListMap(id);
	}
	
	private List<Map<String, Object>> getListMap(String parentId) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		List<DataApiOnlineEntity> apiList = dataApiOnlineRepository.findByParentIdOrderByCreateTime(parentId);
		for(DataApiOnlineEntity apiOnlineEntity : apiList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", apiOnlineEntity.getId());
			map.put("name", apiOnlineEntity.getName());
			map.put("parentId", apiOnlineEntity.getParentId());
			map.put("type", apiOnlineEntity.getType());
			
			if(apiOnlineEntity.getType().equals("folder")) {
				map.put("children", getListMap(apiOnlineEntity.getId()));
			}else {
				String json = dataApiOnlineInfoRepository.findById(apiOnlineEntity.getId()).orElse(null).getFormData();
				map.put("ApiForm", Y9JsonUtil.readHashMap(json));
			}
			listMap.add(map);
		}
		return listMap;
	}

}