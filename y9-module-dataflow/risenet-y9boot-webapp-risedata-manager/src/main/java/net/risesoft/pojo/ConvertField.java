package net.risesoft.pojo;

import lombok.Data;

/**
 * 数据转换信息
 * @author pzx
 *
 */
@Data
public class ConvertField {

	/**
	 * 字段名称
	 */
	private String fieldName;
	
	/**
	 * 原始数据
	 */
	private String oldData;
	
	/**
	 * 更新数据
	 */
	private String newData;
	
}