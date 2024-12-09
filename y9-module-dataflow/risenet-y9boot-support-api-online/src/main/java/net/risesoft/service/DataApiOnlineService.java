package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.DataApiOnlineEntity;
import net.risesoft.y9public.entity.DataApiOnlineInfoEntity;

public interface DataApiOnlineService {

	/**
	 * 根据id删除接口数据
	 * @param id
	 * @return
	 */
	Y9Result<List<String>> deleteData(String id);

	/**
	 * 保存接口信息
	 * @param entity
	 * @return
	 */
	Y9Result<DataApiOnlineEntity> saveData(DataApiOnlineEntity entity, DataApiOnlineInfoEntity infoEntity);
	
	/**
	 * 获取tree
	 * @param id
	 * @return
	 */
	List<Map<String, Object>> getTree(String id);
	
}
