package net.risesoft.pojo;

import lombok.Data;

@Data
public class TaskCoreModel {

	private String id;
	
	/**
	 * 任务id
	 */
	private String taskId;
	
	/**
	 * 参数表id
	 */
	private String argsId;

	/**
	 * 参数名称
	 */
	private String keyName;
	
	/**
	 * 参数值
	 */
	private String value;
	
	/**
	 * 种类
	 */
	private String dataType;
	
	/**
	 * 类别：执行器-executor，交换机-exchange，输入/输出通道-channel，其它配置-plugs
	 */
	private String typeName;
	
	/**
	 * 序号
	 */
	private Integer sequence;

}