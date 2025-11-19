package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import net.risesoft.security.ConcurrentSecurity;
import net.risesoft.security.SecurityManager;
import net.risesoft.dto.DataBusinessDTO;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataBusinessService;
import net.risesoft.y9public.entity.DataBusinessEntity;
import net.risesoft.y9public.repository.DataBusinessRepository;
import net.risesoft.y9public.repository.DataTaskRepository;
import net.risesoft.y9public.repository.spec.DataBusinessSpecification;

@Service(value = "dataBusinessService")
@RequiredArgsConstructor
public class DataBusinessServiceImpl implements DataBusinessService {
	
	private final DataTaskRepository dataTaskRepository;
	
	private final DataBusinessRepository dataBusinessRepository;
	
	private final SecurityManager securityManager;
	
	private final ModelMapper modelMapper;
	
	@Override
	public Page<DataBusinessEntity> findByNamePage(String name, String parentId, int page, int rows) {
		if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "createTime"));
        if(StringUtils.isBlank(parentId)) {
        	parentId = "0";
        }
        ConcurrentSecurity security = securityManager.getConcurrentSecurity();
        DataBusinessSpecification spec = new DataBusinessSpecification(parentId, name, security.getJobTypes());
		return dataBusinessRepository.findAll(spec, pageable);
	}

	@Override
	public DataBusinessEntity getById(String id) {
		return dataBusinessRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> deleteData(String id) {
		ConcurrentSecurity security = securityManager.getConcurrentSecurity();
		List<String> ids = security.getJobTypes();
		if(ids.size() > 0 && !ids.contains(id)) {
			return Y9Result.failure("没有该分类权限，无法对其进行操作");
		}
		if(dataTaskRepository.countByBusinessId(id) > 0) {
			return Y9Result.failure("分类下存在任务，无法删除");
		}
		List<String> list = dataBusinessRepository.findByParentId(id);
		if(list != null && list.size() > 0) {
			if(dataTaskRepository.countByBusinessIdIn(list) > 0) {
				return Y9Result.failure("子分类下存在任务，无法删除");
			}
			dataBusinessRepository.deleteAllById(list);
		}
		dataBusinessRepository.deleteById(id);
		return Y9Result.successMsg("删除成功");
	}

	@Override
	public List<DataBusinessEntity> findByParentId(String parentId) {
		return dataBusinessRepository.findByParentIdOrderByCreateTime(parentId);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> saveData(DataBusinessDTO businessDTO) {
		try {
			DataBusinessEntity entity = modelMapper.map(businessDTO, DataBusinessEntity.class);
			if (StringUtils.isNotBlank(entity.getName())) {
				// 判断是否顶节点，当权限不是管理员时，不允许添加顶节点
				boolean top = StringUtils.isBlank(entity.getId()) && StringUtils.isBlank(entity.getParentId());
				ConcurrentSecurity security = securityManager.getConcurrentSecurity();
				if(security.getJobTypes().size() > 0 && top) {
					return Y9Result.failure("权限不够，只能添加子节点");
				}
				DataBusinessEntity dataBusinessEntity = null;
				if (StringUtils.isBlank(entity.getId())) {
					dataBusinessEntity = new DataBusinessEntity();
					dataBusinessEntity.setId(Y9IdGenerator.genId());
					dataBusinessEntity.setParentId(entity.getParentId());
				}else {
					dataBusinessEntity = getById(entity.getId());
				}
				dataBusinessEntity.setName(entity.getName());
				if(StringUtils.isBlank(dataBusinessEntity.getParentId())) {
					dataBusinessEntity.setParentId("0");
				}
				return Y9Result.successMsg("保存成功");
			}else {
				return Y9Result.failure("数据不能为空");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("保存失败：" + e.getMessage());
		}
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
		ConcurrentSecurity security = securityManager.getConcurrentSecurity();
		List<String> ids = security.getJobTypes();
		if(ids.size() > 0) {
			return dataBusinessRepository.findByIdIn(ids);
		}
		return dataBusinessRepository.findAll();
	}

	@Override
	public String getNameById(String id) {
		DataBusinessEntity dataBusinessEntity = getById(id);
		if(dataBusinessEntity != null) {
			return dataBusinessEntity.getName();
		}
		return "";
	}

}