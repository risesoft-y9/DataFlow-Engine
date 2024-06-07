package net.risedata.rpc.exceptions;

/**
 * 代理对象时出现的异常
 * 
 * @author libo 2021年7月28日
 */
public class ProxyException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProxyException(String msg) {
		super(msg);
	}
}
