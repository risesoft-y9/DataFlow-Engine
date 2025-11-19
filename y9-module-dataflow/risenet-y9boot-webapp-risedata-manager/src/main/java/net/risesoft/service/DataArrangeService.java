package net.risesoft.service;

import java.util.Map;

import org.springframework.data.domain.Page;

import net.risesoft.dto.DataArrangeDTO;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9public.entity.DataArrangeEntity;

public interface DataArrangeService {

	/**
	 * 根据条件获取任务编排数据列表
	 * @param name
	 * @param pattern
	 * @param page
	 * @param rows
	 * @return
	 */
	Page<DataArrangeEntity> searchPage(String name, Integer pattern, int page, int rows);

	/**
	 * 根据ID获取数据
	 * @return
	 */
	DataArrangeEntity getById(String id);

	/**
	 * 根据id删除任务编排数据
	 */
	Y9Result<String> deleteData(String id);

	/**
	 * 保存任务编排信息
	 * @param arrangeDTO
	 * @return
	 */
	Y9Result<String> saveData(DataArrangeDTO arrangeDTO);
	
	/**
	 * 保存流程内容
	 * @param xmlData
	 * @return
	 */
	Y9Result<String> saveXml(String id, String xmlData, String parseData);
	
	/**
	 * 启动任务流程
	 * @param id
	 * @return
	 */
	Y9Result<String> executeProcess(String id);
	
	/**
	 * 获取编排任务的日志
	 * @param id
	 * @return
	 */
	Y9Page<Map<String, Object>> getLogList(String id, int page, int rows);
	
}
