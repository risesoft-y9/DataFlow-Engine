package net.risedata.jdbc.commons;


/**
 * 分页
 * @author libo
 *2020年10月9日
 */
public class PageableUtil{
    /**
     * 拿到分页的sql
     * @param baseSql
     * @param pageIndex
     * @param rows
     * @return
     */
	public static String getPageSql(String baseSql,Integer pageIndex,Integer rows){
		if(baseSql==null)return null;
		pageIndex=pageIndex==null?1:pageIndex;
		rows=rows==null?10:rows;
		return "select * from (select commonalias.*,rownum rn from ("
		+baseSql+") commonalias where rownum <= "+pageIndex*rows+ ") where  rn >"+(pageIndex*rows-rows);
	}
	


}