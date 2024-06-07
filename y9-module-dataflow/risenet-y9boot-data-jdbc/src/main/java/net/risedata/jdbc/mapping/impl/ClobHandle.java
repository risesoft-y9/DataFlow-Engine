package net.risedata.jdbc.mapping.impl;


import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;


public class ClobHandle extends SimpleHandleMapping<Clob> {

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == Clob.class;
	}

	@Override
	public Clob getValue(ResultSet set, String field) throws SQLException {
		return set.getClob(field);
	}

}
