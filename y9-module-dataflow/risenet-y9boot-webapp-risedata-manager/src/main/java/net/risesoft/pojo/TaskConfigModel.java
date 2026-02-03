package net.risesoft.pojo;

import lombok.Data;

/**
 * 任务配置信息
 * @author pzx
 *
 */
@Data
public class TaskConfigModel {

	private String id;
	
	/**
	 * 任务id
	 */
	private String taskId;
	
	/**
	 * 源头-执行类名称
	 */
	private String sourceName;
	
	/**
	 * 源头-数据源id
	 */
	private String sourceId;
	
	/**
	 * 源头-数据源类型
	 */
	private String sourceType;
	
	/**
	 * 源头-数据表
	 */
	private String sourceTable;
	
	/**
	 * 源头-字段列
	 */
	private String sourceCloumn;
	
	/**
	 * jdbc查询时ResultSet的每次读取记录数
	 */
	private Integer fetchSize;
	
	/**
	 * where条件语句
	 */
	private String whereSql;
	
	/**
	 * 切分字段
	 */
	private String splitPk;
	
	/**
	 * 是否精准切分，相当于groupby
	 */
	private Boolean precise = false;
	
	/**
	 * 切分为多少块，当没设置精准切分时生效
	 */
	private Integer tableNumber;
	
	/**
	 * 外部切分因素，当tableNumber没设值时生效
	 */
	private Integer splitFactor;
	
	/**
	 * 目标-执行类名称
	 */
	private String targeName;
	
	/**
	 * 目标-数据源id
	 */
	private String targetId;
	
	/**
	 * 目标-数据源类型
	 */
	private String targetType;
	
	/**
	 * 目标-数据表
	 */
	private String targetTable;
	
	/**
	 * 目标-输出类型：insert、update
	 */
	private String writerType;
	
	/**
	 * 输出类型为update时所选字段
	 */
	private String updateField;
	
	/**
	 * 目标-字段列
	 */
	private String targetCloumn;
	
	/**
	 * 异字段自动配置
	 */
	private Boolean fieldAuto = false;
	
	/**
	 * 数据转换自动配置
	 */
	private Boolean valueAuto = false;

}