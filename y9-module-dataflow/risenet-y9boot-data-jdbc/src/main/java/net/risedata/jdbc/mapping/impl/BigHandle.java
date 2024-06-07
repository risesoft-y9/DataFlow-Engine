package net.risedata.jdbc.mapping.impl;


import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.risedata.jdbc.mapping.CastHandleMapping;

public class BigHandle extends SimpleHandleMapping<BigDecimal> implements CastHandleMapping<BigDecimal> {

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == BigDecimal.class;
	}

	@Override
	public BigDecimal getValue(ResultSet set, String field) throws SQLException {
		return toValue(set, set.getBigDecimal(field));
	}

	@Override
	public BigDecimal toValue(Object o) {
		return new BigDecimal(o.toString());
	}

}
