package net.risedata.jdbc.mapping.impl;


import java.sql.ResultSet;
import java.sql.SQLException;

import net.risedata.jdbc.mapping.CastHandleMapping;

public class LongHandle extends SimpleHandleMapping<Long> implements CastHandleMapping<Long>{

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == Long.class||cla==Long.TYPE;
	}


	@Override
	public Long getValue(ResultSet set, String field) throws SQLException {
		return toValue(set,set.getLong(field));
	}

	@Override
	public Long toValue(Object o) {
		if (o instanceof Long) {
			return (Long)o;
		}
		return Long.valueOf(o.toString());
	}
	


}
