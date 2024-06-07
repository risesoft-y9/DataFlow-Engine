package net.risedata.jdbc.type.sql.impl;


public class StringSqlTypeImpl extends BaseSqlType<String> {

	@Override
	public boolean isHandle(String type) {
		String sqltype = type.toUpperCase();
		return sqltype.indexOf("CHAR")!=-1||sqltype.indexOf("BLOB")!=-1;
	}



}
