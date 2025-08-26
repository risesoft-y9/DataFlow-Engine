package net.risesoft.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DataCacheService;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9public.entity.DataCacheEntity;
import net.risesoft.y9public.repository.DataCacheRepository;

@Service(value = "dataCacheService")
@RequiredArgsConstructor
public class DataCacheServiceImpl implements DataCacheService {
	
	private final DataCacheRepository dataCacheRepository;

	@Override
	public Y9Result<String> get(String key) {
		if(StringUtils.isBlank(key)) {
			return Y9Result.failure("key值不能为空");
		}
		DataCacheEntity cacheEntity = dataCacheRepository.findById(key).orElse(null);
		if(cacheEntity != null) {
			return Y9Result.success(cacheEntity.getCacheValue());
		}
		return Y9Result.success("");
	}

	@Override
	public Y9Result<String> put(String key, Object value) {
		if(StringUtils.isBlank(key)) {
			return Y9Result.failure("key值不能为空");
		}
		try {
			DataCacheEntity cacheEntity = new DataCacheEntity();
			cacheEntity.setCacheKey(key);
			cacheEntity.setCacheValue((String)value);
			dataCacheRepository.save(cacheEntity);
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("put失败：" + e.getMessage());
		}
		return Y9Result.successMsg("存储成功");
	}

	@Override
	public Y9Result<Boolean> remove(String key) {
		if(StringUtils.isBlank(key)) {
			return Y9Result.failure("key值不能为空");
		}
		try {
			dataCacheRepository.deleteById(key);
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure(false, "删除失败");
		}
		return Y9Result.success(true);
	}

	@Override
	public Y9Result<Integer> push(String key, Object value) {
		if(StringUtils.isBlank(key)) {
			return Y9Result.failure("key值不能为空");
		}
		try {
			DataCacheEntity cacheEntity = dataCacheRepository.findById(key).orElse(null);
			if(cacheEntity != null) {
				List<Object> list = Y9JsonUtil.readList(cacheEntity.getCacheValue(), Object.class);
				list.add(value);
				cacheEntity.setCacheValue(Y9JsonUtil.writeValueAsString(list));
				dataCacheRepository.save(cacheEntity);
				return Y9Result.success(list.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("push失败：" + e.getMessage());
		}
		return Y9Result.failure("key值不存在");
	}

	@Override
	public Y9Result<String> poll(String key, int size) {
		if(StringUtils.isBlank(key)) {
			return Y9Result.failure("key值不能为空");
		}
		try {
			DataCacheEntity cacheEntity = dataCacheRepository.findById(key).orElse(null);
			if(cacheEntity != null) {
				List<Object> list = Y9JsonUtil.readList(cacheEntity.getCacheValue(), Object.class);
				// 获取指定数量的数据
				List<Object> olist = list.subList(0, Math.min(size, list.size()));
				// 移除并保存
				list.removeAll(olist);
				cacheEntity.setCacheValue(Y9JsonUtil.writeValueAsString(list));
				dataCacheRepository.save(cacheEntity);
				
				return Y9Result.success(Y9JsonUtil.writeValueAsString(olist));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return Y9Result.failure("poll失败：" + e.getMessage());
		}
		return Y9Result.failure("key值不存在");
	}

}
