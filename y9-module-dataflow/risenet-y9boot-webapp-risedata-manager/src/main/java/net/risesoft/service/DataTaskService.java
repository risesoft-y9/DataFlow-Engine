package net.risesoft.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.pojo.SingleTaskModel;
import net.risesoft.pojo.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.DataTaskEntity;

public interface DataTaskService {

	/**
	 * 分页获取任务列表
	 * @param name
	 * @param businessId
	 * @param page
	 * @param rows
	 * @return
	 */
	Page<DataTaskEntity> findPage(List<String> ids, String name, List<String> businessIds, int page, int rows);

	/**
	 * 根据ID获取同步任务数据
	 * @return
	 */
	TaskModel getById(String id);
	
	/**
	 * 根据id获取单任务信息
	 * @param id
	 * @return
	 */
	SingleTaskModel getSingleTaskById(String id, String type);

	/**
	 * 根据id删除任务数据
	 */
	void deleteData(String id);

	/**
	 * 根据分类获取任务
	 * @param businessId
	 * @return
	 */
	List<DataTaskEntity> findByBusinessId(String businessId);
	
	/**
	 * 保存任务主体信息
	 * @param entity
	 * @return
	 */
	Y9Result<DataTaskEntity> saveData(DataTaskEntity entity);
	
	/**
	 * 保存任务配置
	 * @param taskModel
	 * @return
	 */
	Y9Result<DataTaskEntity> save(TaskModel taskModel);
	
	/**
	 * 删除任务core配置信息
	 * @param id
	 */
	void deleteTaskCore(String id);
	
	/**
	 * 获取任务详情
	 * @param id
	 * @return
	 */
	Map<String, Object> getTaskDetails(String id);
	
	/**
	 * 获取所有任务
	 * @return
	 */
	List<DataTaskEntity> findAll(String businessId);
	
	/**
	 * 根据id获取任务
	 * @param id
	 * @return
	 */
	DataTaskEntity findById(String id);
	
	/**
	 * 保存单节点任务
	 * @param singleTaskModel
	 * @return
	 */
	Y9Result<DataTaskEntity> saveSingleTask(SingleTaskModel singleTaskModel);
	
}
