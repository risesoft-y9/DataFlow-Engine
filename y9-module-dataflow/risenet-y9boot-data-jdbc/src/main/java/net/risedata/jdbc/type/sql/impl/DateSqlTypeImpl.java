package net.risedata.jdbc.type.sql.impl;

import java.util.Date;
/**
 * 时间类型的type
 * @author libo
 *2021年2月8日
 */
public class DateSqlTypeImpl extends BaseSqlType<Date> {

	@Override
	public boolean isHandle(String type) {
		return type.toUpperCase().indexOf("DATE") != -1;
	}

}
