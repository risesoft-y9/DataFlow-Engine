package net.risedata.jdbc.mapping.impl;


import java.sql.ResultSet;
import java.sql.SQLException;

import net.risedata.jdbc.mapping.CastHandleMapping;

public class ShortHandle extends SimpleHandleMapping<Short> implements CastHandleMapping<Short>{

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == Short.class||cla==Short.TYPE;
	}


	@Override
	public Short getValue(ResultSet set, String field) throws SQLException {
		return toValue(set, set.getShort(field));
	}

	@Override
	public Short toValue(Object o) {
		if (o instanceof Short) {
			return (Short)o;
		}
		return Short.parseShort(o.toString());
	}
	


}
