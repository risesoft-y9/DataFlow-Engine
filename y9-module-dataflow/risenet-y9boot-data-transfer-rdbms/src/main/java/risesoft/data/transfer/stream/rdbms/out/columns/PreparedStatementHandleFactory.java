package risesoft.data.transfer.stream.rdbms.out.columns;

/**
 * 用于生产PreparedStatementHandle
 * 
 * 
 * @typeName PreparedStatementHandleFactory
 * @date 2024年12月26日
 * @author lb
 */
public interface PreparedStatementHandleFactory {
	/**
	 * 是否被这个handle处理
	 * 
	 * @param type
	 * @return
	 */
	boolean isHandle(int type);

	/**
	 * 获取处理对象
	 * 
	 * @return
	 */
	PreparedStatementHandle getPreparedStatementHandle(int type);

}
