package net.risedata.jdbc.mapping.impl;


import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteArrayHandle extends SimpleHandleMapping<byte[]>{

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla ==  byte[].class;
	}



	@Override
	public byte[] getValue(ResultSet set, String field) throws SQLException {
		return set.getBytes(field);
	}

	
	


}
