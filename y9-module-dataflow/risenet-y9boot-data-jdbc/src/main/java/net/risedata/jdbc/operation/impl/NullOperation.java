package net.risedata.jdbc.operation.impl;

import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;

/**
 * 占位符 空的 操作实现了operation接口但是没有做任何处理
 * 
 * @author libo 2021年2月9日
 */
public class NullOperation extends SimpleOperation {

	@Override
	public boolean where(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap,
			BeanConfig bc, Map<String, Object> excludeMap) {

		return false;
	}

	@Override
	public int getOperate() {
		return 0;
	}

}
