package net.risesoft.service;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.DataWatermarkEntity;

public interface DataWatermarkService {

	/**
	 * 分页获取水印信息列表
	 * @param page
	 * @param size
	 * @return
	 */
	Page<DataWatermarkEntity> searchPage(int page, int size);

	/**
	 * 根据ID获取数据
	 * @return
	 */
	DataWatermarkEntity getById(String id);

	/**
	 * 根据id删除水印数据
	 */
	Y9Result<String> deleteData(String id);

	/**
	 * 保存水印信息
	 * @param entity
	 * @return
	 */
	Y9Result<DataWatermarkEntity> saveData(DataWatermarkEntity entity);
	
}
