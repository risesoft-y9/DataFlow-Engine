package net.risedata.jdbc.executor.page;

/**
 * 分页的操作</br>
 * 会根据数据库来选择不同的实现类 使用者也可以自定义分页工具
 * 
 * @author libo 2021年2月8日
 */
public interface PageExecutor {
	/**
	 * 返回分页后的sql
	 * 
	 * @param sql      分页前的sql
	 * @param pageNo   当前页数
	 * @param pageSize 大小
	 * @return
	 */
	String getPageSql(String sql, int pageNo, int pageSize);
}
