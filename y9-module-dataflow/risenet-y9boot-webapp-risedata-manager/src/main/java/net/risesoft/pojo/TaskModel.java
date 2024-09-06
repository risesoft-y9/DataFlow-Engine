package net.risesoft.pojo;

import java.util.List;

import lombok.Data;

/**
 * 任务主体信息
 * @author pzx
 *
 */
@Data
public class TaskModel {

	private String id;
	
	/**
	 * 任务名称
	 */
	private String name;
	
	/**
	 * 任务描述
	 */
	private String description;
	
	/**
	 * 业务分类id
	 */
	private String businessId;
	
	/**
	 * 创建者id
	 */
	private String userId;
	
	/**
	 * 创建者名称
	 */
	private String userName;
	
	/**
	 * 副表
	 */
	private TaskConfigModel taskConfigModel;
	
	/**
	 * 配置列表
	 */
	private List<TaskCoreModel> taskCoreList;
	
	/**
	 * 数据脱敏字段
	 */
	private String maskFields;
	
	/**
	 * 数据加密字段
	 */
	private String encrypFields;
	
	/**
	 * 异字段
	 */
	private List<DifferentField> differentField;
	
	/**
	 * 日期格式
	 */
	private List<DateField> dateField;
	
	/**
	 * 数据转换
	 */
	private List<ConvertField> convertField;

}