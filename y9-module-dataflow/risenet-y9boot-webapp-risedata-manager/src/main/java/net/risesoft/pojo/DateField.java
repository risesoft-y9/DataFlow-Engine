package net.risesoft.pojo;

import lombok.Data;

/**
 * 日期格式转换信息
 * @author pzx
 *
 */
@Data
public class DateField {

	/**
	 * 字段名称
	 */
	private String fieldName;
	
	/**
	 * 日期格式
	 */
	private String format;
	
}