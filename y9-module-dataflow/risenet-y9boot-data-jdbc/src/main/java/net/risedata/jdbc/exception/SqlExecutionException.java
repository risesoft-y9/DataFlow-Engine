package net.risedata.jdbc.exception;

/**
 * 执行sql 时出现的异常
 * 
 * @author libo 2021年7月28日
 */
public class SqlExecutionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SqlExecutionException(String msg) {
		super(msg);
	}

}
