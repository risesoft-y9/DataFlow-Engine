package net.risedata.jdbc.type.sql.impl;

import java.sql.Timestamp;

public class TimeSqlTypeImpl extends BaseSqlType<Timestamp> {

	@Override
	public boolean isHandle(String type) {
		return type.toUpperCase().indexOf("TIME")!=-1;
	}



}
