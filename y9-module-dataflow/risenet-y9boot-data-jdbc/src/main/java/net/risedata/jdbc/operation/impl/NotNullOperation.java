package net.risedata.jdbc.operation.impl;

import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;

public class NotNullOperation extends SimpleOperation{
    private static final String NOT_NULL = " is not null ";
	@Override
	public boolean where(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap,
			BeanConfig bc, Map<String, Object> excludeMap) {
		sql.append(field.getColumn()+NOT_NULL);
		return true;
	}

	@Override
	public int getOperate() {
		return 0;
	}

}
