package net.risedata.jdbc.type.sql.impl;

import net.risedata.jdbc.type.sql.SqlType;

public class NumberSqlTypeImpl implements SqlType {

	@Override
	public boolean isHandle(String type) {
		return type.toUpperCase().indexOf("NUMBER") == 0;
	}

	@Override
	public Class<?> getType(String type) {
		if ("NUMBER".equals( type.toUpperCase())) {
			return Long.class;
		}
		return Double.class;
	}

}
