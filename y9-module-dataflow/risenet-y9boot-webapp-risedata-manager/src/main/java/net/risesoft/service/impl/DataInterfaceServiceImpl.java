package net.risesoft.service.impl;

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
import net.risesoft.service.DataInterfaceService;
import net.risesoft.util.DataUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataInterfaceEntity;
import net.risesoft.y9public.entity.DataInterfaceParamsEntity;
import net.risesoft.y9public.repository.DataInterfaceParamsRepository;
import net.risesoft.y9public.repository.DataInterfaceRepository;
import net.risesoft.y9public.repository.DataTaskConfigRepository;
import net.risesoft.y9public.repository.spec.DataInterfaceSpecification;

@Service(value = "dataInterfaceService")
@RequiredArgsConstructor
public class DataInterfaceServiceImpl implements DataInterfaceService {
	
	private final DataInterfaceRepository dataInterfaceRepository;
	private final DataInterfaceParamsRepository dataInterfaceParamsRepository;
	private final DataTaskConfigRepository dataTaskConfigRepository;
	
	@Override
	public Page<DataInterfaceEntity> searchPage(String search, String requestType, Integer dataType, int page, int rows) {
		if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, rows, Sort.by(Sort.Direction.ASC, "createTime"));
        DataInterfaceSpecification spec = new DataInterfaceSpecification(search, requestType, dataType, Y9LoginUserHolder.getTenantId());
		return dataInterfaceRepository.findAll(spec, pageable);
	}

	@Override
	public DataInterfaceEntity getById(String id) {
		return dataInterfaceRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> deleteData(String id) {
		if(dataTaskConfigRepository.findByTableId(id).size() > 0) {
			return Y9Result.failure("已关联任务，无法删除");
		}
		dataInterfaceRepository.deleteById(id);
		return Y9Result.successMsg("删除成功");
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataInterfaceEntity> saveData(DataInterfaceEntity entity) {
		if (StringUtils.isBlank(entity.getId())) {
			entity.setId(Y9IdGenerator.genId());
		}
		entity.setTenantId(Y9LoginUserHolder.getTenantId());
		entity.setUserId(Y9LoginUserHolder.getPersonId());
		entity.setUserName(Y9LoginUserHolder.getUserInfo().getName());
		return Y9Result.success(dataInterfaceRepository.save(entity), "保存成功");
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataInterfaceParamsEntity> saveData(DataInterfaceParamsEntity entity) {
		if (StringUtils.isBlank(entity.getId())) {
			entity.setId(Y9IdGenerator.genId());
		}
		entity.setTenantId(Y9LoginUserHolder.getTenantId());
		entity.setUserId(Y9LoginUserHolder.getPersonId());
		entity.setUserName(Y9LoginUserHolder.getUserInfo().getName());
		return Y9Result.success(dataInterfaceParamsRepository.save(entity), "保存成功");
	}

	@Override
	public List<DataInterfaceParamsEntity> findByParentIdAndDataType(String parentId, Integer dataType) {
		return dataInterfaceParamsRepository.findByParentIdAndDataType(parentId, dataType);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> deleteParam(String id) {
		dataInterfaceParamsRepository.deleteById(id);
		return Y9Result.successMsg("删除成功");
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> saveResponseParams(String data) {
		if(StringUtils.isBlank(data)) {
			return Y9Result.failure("返回值为空，保存不了");
		}
		Map<String, Object> map = Y9JsonUtil.readHashMap(data);
		List<Map<String, Object>> listMap = null;
		try {
			listMap = (List<Map<String, Object>>) map.get("jsonData");
		} catch (Exception e) {
			return Y9Result.failure("接口返回格式不对，data值必须为List<Map<String, Object>>");
		}
		if(listMap == null) {
			return Y9Result.successMsg("返回值data为空，不用保存返回参数");
		}
		Map<String, Object> rmap = listMap.get(0);
		String parentId = (String)map.get("interfaceId");
		// 先删除已存在数据
		dataInterfaceParamsRepository.deleteByParentIdAndDataType(parentId, 1);
		for (String key : rmap.keySet()) {
			DataInterfaceParamsEntity dataInterfaceParamsEntity = dataInterfaceParamsRepository.findByParentIdAndDataTypeAndParamName(parentId, 1, key);
			if(dataInterfaceParamsEntity == null) {
				dataInterfaceParamsEntity = new DataInterfaceParamsEntity();
				dataInterfaceParamsEntity.setDataType(1);
				dataInterfaceParamsEntity.setId(Y9IdGenerator.genId());
				dataInterfaceParamsEntity.setParamName(key);
				dataInterfaceParamsEntity.setParamType(DataUtils.checkType(rmap.get(key)));
				dataInterfaceParamsEntity.setParentId(parentId);
				dataInterfaceParamsEntity.setRemark("");
				dataInterfaceParamsEntity.setReqType("");
				dataInterfaceParamsRepository.save(dataInterfaceParamsEntity);
			}
		}
		return Y9Result.successMsg("保存成功");
	}

	@Override
	public List<DataInterfaceEntity> findByPattern(Integer pattern) {
		return dataInterfaceRepository.findByPatternAndTenantId(pattern, Y9LoginUserHolder.getTenantId());
	}

}