package net.risedata.jdbc.operation.impl;

import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.operation.Custom;
import net.risedata.jdbc.operation.Where;

/**
 * 自定义的where 操作
 * 
 * @author libo 2020年10月22日
 */
public class CustomOperation extends SimpleOperation {
	Custom ct;
	int level = -1;

	/**
	 * 自定义操作
	 * 
	 * @param ct
	 * @param level 排序级别 越大越靠后
	 */
	public CustomOperation(Custom ct, int level) {
		this.ct = ct;
		this.level = level;
	}

	/**
	 * 自定义的操作
	 * 
	 * @param ct
	 */
	public CustomOperation(Custom ct) {
		this.ct = ct;
	}

	@Override
	public boolean where(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap,
			BeanConfig bc, Map<String, Object> excludeMap) {
		return ct.where(new Where(sql, field, valueMap, args, excludeMap, bc));
	}

	@Override
	public int getOperate() {
		return level;
	}

}
