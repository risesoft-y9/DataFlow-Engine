package net.risesoft.pojo;

import lombok.Data;

/**
 * 异字段信息
 * @author pzx
 *
 */
@Data
public class DifferentField {

	/**
	 * 源字段名称
	 */
	private String source;
	
	/**
	 * 目标字段名称
	 */
	private String target;
	
}