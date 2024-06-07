package net.risedata.jdbc.mapping.impl;


import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BlobHandle  extends SimpleHandleMapping<Blob>{

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == Blob.class;
	}
	@Override
	public Blob getValue(ResultSet set, String field) throws SQLException {
		 
		return set.getBlob(field);
	}
	


}
