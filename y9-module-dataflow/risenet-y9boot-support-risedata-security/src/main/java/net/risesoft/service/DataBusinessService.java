package net.risesoft.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.DataBusinessEntity;

public interface DataBusinessService {

	/**
	 * 查询业务分类数据分页列表
	 */
	Page<DataBusinessEntity> findByNamePage(String name, String parentId, int page, int rows);

	/**
	 * 根据ID获取数据
	 * @return
	 */
	DataBusinessEntity getById(String id);
	
	/**
	 * 根据id获取名称
	 * @param id
	 * @return
	 */
	String getNameById(String id);

	/**
	 * 根据id删除数据
	 */
	Y9Result<String> deleteData(String id);

	/**
	 * 根据父节点获取子项列表
	 * @param parentId
	 * @return
	 */
	List<DataBusinessEntity> findByParentId(String parentId);
	
	/**
	 * 保存业务分类信息
	 * @param entity
	 * @return
	 */
	Y9Result<DataBusinessEntity> saveData(DataBusinessEntity entity);
	
	/**
	 * 获取业务分类树状图
	 * @return
	 */
	List<Map<String, Object>> getTree();
	
	/**
	 * 获取全部数据
	 * @return
	 */
	List<DataBusinessEntity> findAll();
	
}
