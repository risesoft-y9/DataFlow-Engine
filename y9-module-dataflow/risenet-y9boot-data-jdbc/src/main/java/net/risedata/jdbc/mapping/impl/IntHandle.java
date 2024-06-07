package net.risedata.jdbc.mapping.impl;


import java.sql.ResultSet;
import java.sql.SQLException;

import net.risedata.jdbc.mapping.CastHandleMapping;

public class IntHandle extends SimpleHandleMapping<Integer> implements CastHandleMapping<Integer> {

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == Integer.class || cla == Integer.TYPE;
	}

	@Override
	public Integer getValue(ResultSet set, String field) throws SQLException {
		return toValue(set, set.getInt(field));
	}

	@Override
	public Integer toValue(Object o) {
		if (o instanceof Integer) {
			return (Integer) o;
		}
		return Integer.parseInt(o.toString());
	}

}
