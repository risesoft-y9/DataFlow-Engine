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
import net.risesoft.security.ConcurrentSecurity;
import net.risesoft.security.SecurityManager;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataMappingService;
import net.risesoft.util.DataServiceUtil;
import net.risesoft.y9public.entity.DataMappingArgsEntity;
import net.risesoft.y9public.entity.DataMappingEntity;
import net.risesoft.y9public.repository.DataMappingArgsRepository;
import net.risesoft.y9public.repository.DataMappingRepository;
import net.risesoft.y9public.repository.DataTaskCoreRepository;
import net.risesoft.y9public.repository.spec.DataMappingSpecification;

@Service(value = "dataMappingService")
@RequiredArgsConstructor
public class DataMappingServiceImpl implements DataMappingService {
	
	private final DataMappingRepository dataMappingRepository;
	private final DataMappingArgsRepository dataMappingArgsRepository;
	private final DataTaskCoreRepository dataTaskCoreRepository;
	private final SecurityManager securityManager;
	
	@Override
	public Page<DataMappingEntity> getDataPage(String typeName, String className, int page, int rows) {
		if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "createTime"));
        DataMappingSpecification spec = new DataMappingSpecification(typeName, className);
		return dataMappingRepository.findAll(spec, pageable);
	}

	@Override
	public DataMappingEntity getById(String id) {
		return dataMappingRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> deleteData(String id) {
		// 判断有没有操作权限
		if(!isSystemManager()) {
			return Y9Result.failure("没有操作权限");
		}
		if(dataTaskCoreRepository.countByArgsId(id) > 0) {
			return Y9Result.failure("存在任务在使用，无法删除");
		}
		dataMappingRepository.deleteById(id);
		dataMappingArgsRepository.deleteByMappingId(id);
		return Y9Result.successMsg("删除成功");
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataMappingEntity> saveData(DataMappingEntity entity) {
		// 判断有没有操作权限
		if(!isSystemManager()) {
			return Y9Result.failure("没有操作权限");
		}
		if (entity != null && StringUtils.isNotBlank(entity.getClassName())) {
			DataMappingEntity info = dataMappingRepository.findByTypeNameAndClassNameAndFuncType(entity.getTypeName(), 
					entity.getClassName(), entity.getFuncType());
			if(info != null && !info.getId().equals(entity.getId())) {
				return Y9Result.failure("数据已存在，不能重复添加");
			}
			if (StringUtils.isBlank(entity.getId())) {
				entity.setId(Y9IdGenerator.genId());
			}
			return Y9Result.success(dataMappingRepository.save(entity), "保存成功");
		}
		return Y9Result.failure("数据不能为空");
	}

	@Override
	public Page<DataMappingArgsEntity> getArgsDataPage(String mappingId, int page, int rows) {
		if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.DESC, "createTime"));
		return dataMappingArgsRepository.findByMappingId(mappingId, pageable);
	}

	@Override
	public DataMappingArgsEntity getArgsById(String id) {
		return dataMappingArgsRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> deleteArgs(String id) {
		// 判断有没有操作权限
		if(!isSystemManager()) {
			return Y9Result.failure("没有操作权限");
		}
		if(dataTaskCoreRepository.countByArgsId(id) > 0) {
			return Y9Result.failure("存在任务在使用，无法删除");
		}
		dataMappingArgsRepository.deleteById(id);
		return Y9Result.successMsg("删除成功");
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataMappingArgsEntity> saveArgsData(DataMappingArgsEntity entity) {
		// 判断有没有操作权限
		if(!isSystemManager()) {
			return Y9Result.failure("没有操作权限");
		}
		if (entity != null && StringUtils.isNotBlank(entity.getName())) {
			DataMappingArgsEntity info = dataMappingArgsRepository.findByNameAndMappingId(entity.getName(), entity.getMappingId());
			if(info != null && !info.getId().equals(entity.getId())) {
				return Y9Result.failure("数据已存在，不能重复添加");
			}
			if (StringUtils.isBlank(entity.getId())) {
				entity.setId(Y9IdGenerator.genId());
			}
			return Y9Result.success(dataMappingArgsRepository.save(entity), "保存成功");
		}
		return Y9Result.failure("数据不能为空");
	}

	@Override
	public List<DataMappingEntity> findByTypeNameAndClassNameLike(String typeName, String className) {
		if(StringUtils.isNotBlank(className)) {
			return dataMappingRepository.findByTypeNameAndClassNameContaining(typeName, className);
		}
		return dataMappingRepository.findByTypeName(typeName);
	}

	@Override
	public List<Map<String, Object>> findByTypeName(String typeName) {
		List<Map<String, Object>> listMap = new ArrayList<Map<String,Object>>();
		List<String> list = dataMappingRepository.getByTypeName(typeName);
		for(String data : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("title", DataServiceUtil.getTitle(data));
			map.put("classList", dataMappingRepository.findByTypeNameAndFuncType(typeName, data));
			listMap.add(map);
		}
		return listMap;
	}

	@Override
	public List<DataMappingArgsEntity> findByMappingId(String mappingId) {
		return dataMappingArgsRepository.findByMappingId(mappingId);
	}

	@Override
	public List<DataMappingEntity> findByTypeNameAndFuncType(String typeName, String funcType) {
		if(StringUtils.isBlank(funcType)) {
			return dataMappingRepository.findByTypeName(typeName);
		}
		return dataMappingRepository.findByTypeNameAndFuncType(typeName, funcType);
	}
	
	private boolean isSystemManager() {
		ConcurrentSecurity security = securityManager.getConcurrentSecurity();
		return security.isSystemManager();
	}

}