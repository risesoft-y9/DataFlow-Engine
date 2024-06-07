package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataBusinessService;
import net.risesoft.y9public.entity.DataBusinessEntity;
import net.risesoft.y9public.repository.DataBusinessRepository;
import net.risesoft.y9public.repository.spec.DataBusinessSpecification;

@Service(value = "dataBusinessService")
@RequiredArgsConstructor
public class DataBusinessServiceImpl implements DataBusinessService {
	
	private final DataBusinessRepository dataBusinessRepository;
	
	@Override
	public Page<DataBusinessEntity> findByNamePage(String name, String parentId, int page, int rows) {
		if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "createTime"));
        if(StringUtils.isBlank(parentId)) {
        	parentId = "0";
        }
        DataBusinessSpecification spec = new DataBusinessSpecification(parentId, name);
		return dataBusinessRepository.findAll(spec, pageable);
	}

	@Override
	public DataBusinessEntity getById(String id) {
		return dataBusinessRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteData(String id) {
		List<DataBusinessEntity> list = dataBusinessRepository.findByParentIdOrderByCreateTime(id);
		if(list != null && list.size() > 0) {
			dataBusinessRepository.deleteAll(list);
		}
		dataBusinessRepository.deleteById(id);
	}

	@Override
	public List<DataBusinessEntity> findByParentId(String parentId) {
		return dataBusinessRepository.findByParentIdOrderByCreateTime(parentId);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataBusinessEntity> saveData(DataBusinessEntity entity) {
		if (entity != null && StringUtils.isNotBlank(entity.getName())) {
			if (StringUtils.isBlank(entity.getId())) {
				entity.setId(Y9IdGenerator.genId());
				if(StringUtils.isBlank(entity.getParentId())) {
					entity.setParentId("0");
				}
			}
			return Y9Result.success(dataBusinessRepository.save(entity), "保存成功");
		}
		return Y9Result.failure("数据不能为空");
	}

	@Override
	public List<Map<String, Object>> getTree() {
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		List<DataBusinessEntity> list = dataBusinessRepository.findByParentIdOrderByCreateTime("0");
		for(DataBusinessEntity business : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", business.getId());
			map.put("name", business.getName());
			List<Map<String, Object>> childMap = new ArrayList<Map<String,Object>>();
			List<DataBusinessEntity> childList = dataBusinessRepository.findByParentIdOrderByCreateTime(business.getId());
			if(childList != null && childList.size() > 0) {
				for(DataBusinessEntity child : childList) {
					Map<String, Object> rmap = new HashMap<String, Object>();
					rmap.put("id", child.getId());
					rmap.put("name", child.getName());
					childMap.add(rmap);
				}
			}
			map.put("childs", childMap);
			listMap.add(map);
		}
		return listMap;
	}

	@Override
	public List<DataBusinessEntity> findAll() {
		return dataBusinessRepository.findAll();
	}


}