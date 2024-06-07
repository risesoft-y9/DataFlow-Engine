package net.risedata.jdbc.exception;

/**
 * 通常为创建实例时出错
 * 
 * @author libo 2021年6月24日
 */
public class InstanceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InstanceException(String msg) {
		super(msg);
	}
}
