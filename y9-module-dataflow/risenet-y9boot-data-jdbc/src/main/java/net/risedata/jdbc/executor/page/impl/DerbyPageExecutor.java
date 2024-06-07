package net.risedata.jdbc.executor.page.impl;

import net.risedata.jdbc.executor.page.PageExecutor;

/**
 * mysql的实现类
 * 
 * @author libo 2021年2月8日
 */
public class DerbyPageExecutor implements PageExecutor {

	private String rownum = " ROW_NUMBER() OVER() AS rownum,";

	@Override
	public String getPageSql(String sql, int pageIndex, int rows) {
		int selectIndex = sql.indexOf("SELECT");
		String sqlPre = sql.substring(0, selectIndex + 6);
		String sqlSuffix = sql.substring(selectIndex + 6);
		StringBuilder sqlBuilder = new StringBuilder();

		if (pageIndex == 1) {
			return sqlBuilder.append("select * from (").append(sqlPre).append(rownum).append(sqlSuffix)
					.append(") as derby where rownum <= ").append(pageIndex * rows).toString();
		}
		return sqlBuilder.append("select * from (").append(sqlPre).append(rownum).append(sqlSuffix)
				.append(") as derby where rownum <= ").append(pageIndex * rows).append(" and rownum > ")
				.append(pageIndex * rows - rows).toString();
	}

}
