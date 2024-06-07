package net.risedata.jdbc.executor.page.impl;

import net.risedata.jdbc.executor.page.PageExecutor;

/**
 * mysql的实现类
 * 
 * @author libo 2021年2月8日
 */
public class MysqlPageExecutor implements PageExecutor {

	@Override
	public String getPageSql(String sql, int pageIndex, int rows) {
		return sql + " limit " + ((pageIndex - 1) * rows) + " ," + rows;
	}

}
