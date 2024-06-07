package net.risedata.jdbc.operation.impl;

import java.util.List;
import java.util.Map;

import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.operation.Operation;

public abstract class SimpleOperation implements Operation {
	public static final String n = " ";

	public static Object getValue(FieldConfig field, Map<String, Object> valueMap) {
		return valueMap.get(field.getFieldName());
	}

	public boolean isNotNull(Object o) {
		return o != null && !"".equals(o.toString());
	}

	@Override
	public boolean update(FieldConfig field, List<Object> args, StringBuilder sql, Map<String, Object> valueMap) {
		Object value = getValue(field, valueMap);
		if (value != null) {
			args.add(value);
			sql.append(" " + field.getColumn() + " = ?");
			return true;
		}
		return false;
	}

	@Override
	public void insert(List<Object> args, List<String> columns, Map<String, Object> valueMap, FieldConfig fc) {
		Object value = getValue(fc, valueMap);// 非空才添加
		if (value == null) {
			return;
		}
		if (columns != null) {
			columns.add(fc.getColumn());
		}
		args.add(value);
	}

	@Override
	public boolean check(StringBuilder sql, Map<String, Object> valueMap, List<Object> args, FieldConfig fc) {
		Object value = getValue(fc, valueMap);
		if (value != null) {
			sql.append(fc.getColumn() + " = ?");
			args.add(value);
			return true;
		}
		return false;
	}
}
