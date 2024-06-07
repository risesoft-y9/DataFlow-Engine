package net.risedata.jdbc.executor.page.impl;

import net.risedata.jdbc.executor.page.PageExecutor;

/**
 * oracle的实现类
 * 
 * @author libo 2021年2月8日
 */
public class OraclePageExecutor implements PageExecutor {

	@Override
	public String getPageSql(String sql, int pageIndex, int rows) {
		if (pageIndex == 1) {
			return "select * from (" + sql + ")  where rownum <= " + pageIndex * rows;
		}
		return "select * from (select commonalias.*,rownum rn from (" + sql + ") commonalias where rownum <= "
				+ pageIndex * rows + ") where  rn >" + (pageIndex * rows - rows);
	}

}
