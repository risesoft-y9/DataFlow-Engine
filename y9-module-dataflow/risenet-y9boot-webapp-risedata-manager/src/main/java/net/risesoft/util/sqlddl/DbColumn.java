package net.risesoft.util.sqlddl;

import java.io.Serializable;

import lombok.Data;

@Data
public class DbColumn implements Serializable {

	private static final long serialVersionUID = -7176298428774384422L;

	/**
	 * 列名
	 */
	private String column_name;
	
	private String column_name_old;

	/**
	 * 字段类型
	 */
	private String type_name;
	private int data_type;

	/**
	 * 字段长度
	 */
	private String data_length;
	
	/**
	 * 是否主键
	 */
	private Boolean primaryKey;

	/**
	 * 能否为空
	 */
	private Boolean nullable;

	/**
	 * 是否创建索引
	 */
	private Boolean isCreateIndex;

	/**
	 * 字段备注，用来中文化
	 */
	private String comment;
	
	/**
	 * 字段状态
	 */
	private Boolean isState;
	
	/**
	 * 所属表名
	 */
	private String table_name;

}
