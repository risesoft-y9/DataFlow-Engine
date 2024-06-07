package net.risedata.jdbc.operation;

import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;

/**
 * sql操作符
 * 
 * @author libo 2020年10月9日
 */
public interface Operation {
	/**
	 * 
	 * @param o     查詢的對象
	 * @param field 字段
	 * @param args  參數集合
	 * @param sql   sql
	 * @param index 當前是第幾個參數
	 * @return
	 */
	boolean where(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap, BeanConfig bc,
			Map<String, Object> excludeMap);

	/**
	 * update的时候走此方法
	 * 
	 * @param o        对象
	 * @param field    字段配置
	 * @param args     参数
	 * @param sql      sql
	 * @param index    index
	 * @param valueMap 格外指定的value
	 * @return
	 */
	boolean update(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap);

	/**
	 * 优先级越低越优先
	 * 
	 * @return
	 */
	int getOperate();

	/**
	 * 插入操作
	 * 
	 * @param args     参数
	 * @param columns  记录 哪些插入的colums
	 * @param valueMap 所有的value集合
	 * @param fc       字段配置
	 */
	void insert(List<Object> args, List<String> columns, Map<String, Object> valueMap, FieldConfig fc);

	/**
	 * 检查时候调用
	 * 
	 * @param sql
	 * @param valueMap
	 * @return
	 */
	boolean check(StringBuilder sql, Map<String, Object> valueMap, List<Object> args, FieldConfig fc);

}
