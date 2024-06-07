package risesoft.data.transfer.core.close;

/**
 * 可以被关闭的
 * 
 * @typeName Closed
 * @date 2023年12月8日
 * @author lb
 */
public interface Closed {
	/**
	 * 关闭 通常代表着上游处理任务结束
	 */
	void close() throws Exception;
}
