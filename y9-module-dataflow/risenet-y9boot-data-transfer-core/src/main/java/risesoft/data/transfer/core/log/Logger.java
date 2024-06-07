package risesoft.data.transfer.core.log;

/**
 * 日志处理类框架自带
 * 
 * @typeName Logger
 * @date 2023年12月25日
 * @author lb
 */
public interface Logger {
	/**
	 * 是否接受debug
	 * 
	 * @return
	 */
	boolean isDebug();

	/**
	 * 是否接受info
	 * 
	 * @return
	 */
	boolean isInfo();

	/**
	 * 是否接受 error
	 * 
	 * @return
	 */
	boolean isError();

	/**
	 * 是否接收debug
	 * 
	 * @param msg
	 */
	void debug(Object source,String msg);

	/**
	 * 是否接受info
	 * 
	 * @param msg
	 */
	void info(Object source,String msg);

	/**
	 * 是否接受error
	 * 
	 * @param msg
	 */
	void error(Object source,String msg);
}
