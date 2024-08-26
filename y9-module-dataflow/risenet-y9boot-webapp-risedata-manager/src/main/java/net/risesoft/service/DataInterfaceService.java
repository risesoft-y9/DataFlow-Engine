package net.risesoft.service;

import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.DataInterfaceEntity;
import net.risesoft.y9public.entity.DataInterfaceParamsEntity;

public interface DataInterfaceService {

	/**
	 * 分页获取接口信息列表
	 * @param search
	 * @param requestType
	 * @param dataType
	 * @param page
	 * @param rows
	 * @return
	 */
	Page<DataInterfaceEntity> searchPage(String search, String requestType, Integer dataType, int page, int rows);

	/**
	 * 根据ID获取数据
	 * @return
	 */
	DataInterfaceEntity getById(String id);

	/**
	 * 根据id删除接口数据
	 */
	Y9Result<String> deleteData(String id);

	/**
	 * 保存接口信息
	 * @param entity
	 * @return
	 */
	Y9Result<DataInterfaceEntity> saveData(DataInterfaceEntity entity);
	
	/**
	 * 保存接口参数信息
	 * @param entity
	 * @return
	 */
	Y9Result<DataInterfaceParamsEntity> saveData(DataInterfaceParamsEntity entity);
	
	/**
	 * 获取接口参数列表
	 * @param parentId
	 * @return
	 */
	List<DataInterfaceParamsEntity> findByParentIdAndDataType(String parentId, Integer dataType);
	
	/**
	 * 删除接口参数
	 * @param id
	 * @return
	 */
	Y9Result<String> deleteParam(String id);
	
	/**
	 * 保存接口返回字段信息
	 * @param data
	 * @return
	 */
	Y9Result<String> saveResponseParams(String data);
	
}
