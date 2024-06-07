package net.risesoft.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.DataMappingArgsEntity;
import net.risesoft.y9public.entity.DataMappingEntity;

public interface DataMappingService {

	/**
	 * 查询映射数据分页列表
	 */
	Page<DataMappingEntity> getDataPage(String typeName, String className, int page, int rows);

	/**
	 * 根据ID获取数据
	 * @return
	 */
	DataMappingEntity getById(String id);

	/**
	 * 根据id删除数据
	 * 
	 */
	Y9Result<String> deleteData(String id);

	/**
	 * 保存映射数据
	 * @param entity
	 * @return
	 */
	Y9Result<DataMappingEntity> saveData(DataMappingEntity entity);
	
	/**
	 * 根据类别获取映射数据
	 * @param typeName
	 * @return
	 */
	List<DataMappingEntity> findByTypeNameAndClassNameLike(String typeName, String className);
	
	/**
	 * 获取映射参数分页列表
	 * @param page
	 * @param rows
	 * @return
	 */
	Page<DataMappingArgsEntity> getArgsDataPage(String mappingId, int page, int rows);
	
	/**
	 * 根据id获取参数信息
	 * @param id
	 * @return
	 */
	DataMappingArgsEntity getArgsById(String id);
	
	/**
	 * 根据id删除参数信息
	 * @param id
	 */
	Y9Result<String> deleteArgs(String id);
	
	/**
	 * 保存参数信息
	 * @param entity
	 * @return
	 */
	Y9Result<DataMappingArgsEntity> saveArgsData(DataMappingArgsEntity entity);
	
	/**
	 * 根据类别获取页面信息
	 * @param typeName
	 * @return
	 */
	List<Map<String, Object>> findByTypeName(String typeName);
	
	/**
	 * 根据映射表id获取参数列表
	 * @param mappingId
	 * @return
	 */
	List<DataMappingArgsEntity> findByMappingId(String mappingId);
	
	/**
	 * 根据类别和功能查询
	 * @param typeName
	 * @param funcType
	 * @return
	 */
	List<DataMappingEntity> findByTypeNameAndFuncType(String typeName, String funcType);
	
}
