package net.risedata.jdbc.mapping.impl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.risedata.jdbc.mapping.CastHandleMapping;

public class StringHandle extends SimpleHandleMapping<String> implements CastHandleMapping<String> {

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == String.class || cla == StringBuffer.class || cla == StringBuilder.class;
	}

	@Override
	Object getValue(Field field, String value, Class<?> type) {
		if (type == String.class) {
			return value;
		} else if (type == StringBuffer.class) {
			return new StringBuffer(value);
		} else if (type == StringBuilder.class) {
			return new StringBuilder(value);
		}
		return null;
	}

	@Override
	public String getValue(ResultSet set, String field) throws SQLException {
		return set.getString(field);
	}

	@Override
	public String toValue(Object o) {
		return o.toString();
	}

}
