package net.risedata.jdbc.operation.impl;

import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.operation.Where;

/**
 * 自定义的where 操作
 * 
 * @author libo 2020年10月22日
 */
public abstract class AbstractCustomOperation extends SimpleOperation {

	@Override
	public boolean where(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap,
			BeanConfig bc, Map<String, Object> excludeMap) {
		return handle(new Where(sql, field, valueMap, args, excludeMap, bc));
	}

	protected abstract boolean handle(Where where);

	public int getLevel() {
		return -1;
	}

	@Override
	public int getOperate() {
		return getLevel();
	}

}
