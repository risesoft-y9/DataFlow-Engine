package net.risedata.jdbc.mapping.impl;


import java.sql.ResultSet;
import java.sql.SQLException;

import net.risedata.jdbc.mapping.CastHandleMapping;

/**
 * 小数的处理
 * @author libo
 *2020年10月15日
 */
public class DoubleHandle extends SimpleHandleMapping<Double> implements CastHandleMapping<Double>{
	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == Double.class||cla == Double.TYPE;
	}

	@Override
	public Double getValue(ResultSet set, String field) throws SQLException {
		return toValue(set, set.getDouble(field));
	}
	@Override
	public Double toValue(Object o) {
		if (o instanceof Double) {
			return (double)o;
		}
		return Double.parseDouble(o.toString());
	}


}
