package net.risedata.jdbc.mapping.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.risedata.jdbc.mapping.CastHandleMapping;

public class BooleanHandle extends SimpleHandleMapping<Boolean> implements CastHandleMapping<Boolean>{

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == Boolean.class||cla == boolean.class;
	}

	@Override
	public Boolean getValue(ResultSet set, String field) throws SQLException {
		return set.getInt(field) == 0;
	}

	@Override
	public Boolean toValue(Object o) {
		return (o instanceof Boolean)?(Boolean)o : Boolean.valueOf(o.toString());
	}



	
	


}
