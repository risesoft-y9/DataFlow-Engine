package net.risedata.jdbc.operation;

/**
 * 自定义操作接口
 * 
 * @author libo 2020年11月27日
 */
@FunctionalInterface
public interface Custom {
	/**
	 * 自定义操作接口:参数 {@link Where}
	 * 
	 * @param args
	 * @return {@link Boolean} </br>
	 *         return true 代表此操作增加了条件需要动态判断是否需要添加where 或者and 进行连接条件 </br>
	 *         return false 则不会对sql作出条件的连接
	 */
	boolean where(Where args);
}
