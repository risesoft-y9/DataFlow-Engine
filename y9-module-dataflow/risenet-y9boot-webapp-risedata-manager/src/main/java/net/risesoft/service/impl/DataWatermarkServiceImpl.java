package net.risesoft.service.impl;

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
import net.risesoft.service.DataWatermarkService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9public.entity.DataWatermarkEntity;
import net.risesoft.y9public.repository.DataWatermarkRepository;

@Service(value = "dataWatermarkService")
@RequiredArgsConstructor
public class DataWatermarkServiceImpl implements DataWatermarkService {
	
	private final DataWatermarkRepository dataWatermarkRepository;
	
	@Override
	public Page<DataWatermarkEntity> searchPage(int page, int size) {
		if (page < 0) {
            page = 1;
        }
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "createTime"));
		return dataWatermarkRepository.findAll(pageable);
	}

	@Override
	public DataWatermarkEntity getById(String id) {
		return dataWatermarkRepository.findById(id).orElse(null);
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<String> deleteData(String id) {
		dataWatermarkRepository.deleteById(id);
		return Y9Result.successMsg("删除成功");
	}

	@Override
	@Transactional(readOnly = false)
	public Y9Result<DataWatermarkEntity> saveData(DataWatermarkEntity entity) {
		if (StringUtils.isBlank(entity.getId())) {
			entity.setId(Y9IdGenerator.genId());
		}
		entity.setTenantId(Y9LoginUserHolder.getTenantId());
		entity.setUserId(Y9LoginUserHolder.getPersonId());
		entity.setUserName(Y9LoginUserHolder.getUserInfo().getName());
		return Y9Result.success(dataWatermarkRepository.save(entity), "保存成功");
	}

}