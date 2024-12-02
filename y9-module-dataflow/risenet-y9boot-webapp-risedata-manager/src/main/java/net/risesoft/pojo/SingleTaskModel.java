package net.risesoft.pojo;

import lombok.Data;

/**
 * 单任务主体信息
 * @author pzx
 *
 */
@Data
public class SingleTaskModel {

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
	 * 类型：单任务、同步任务
	 */
	private Integer taskType;
	
	/**
	 * 数据源id
	 */
	private String sourceId;
	
	/**
	 * 数据源类型
	 */
	private String sourceType;
	
	/**
	 * 数据源表
	 */
	private String sourceTable;
	
	/**
	 * SQL语句
	 */
	private String whereSql;
	
	/**
	 * 操作类型
	 */
	private String writerType;

}