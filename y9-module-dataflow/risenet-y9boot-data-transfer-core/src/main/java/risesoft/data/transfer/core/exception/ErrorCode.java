package risesoft.data.transfer.core.exception;

/**
 * 异常编码
 * 
 * @typeName ErrorCode
 * @date 2023年12月4日
 * @author lb
 */
public interface ErrorCode {
	/**
	 * 错误编码
	 * 
	 * @return
	 */
	String getCode();

	/**
	 * 错误描述
	 * 
	 * @return
	 */
	String getDescription();

	/**
	 * 必须提供toString的实现
	 * 
	 * <pre>
	 * &#064;Override
	 * public String toString() {
	 * 	return String.format(&quot;Code:[%s], Description:[%s]. &quot;, this.code, this.describe);
	 * }
	 * </pre>
	 * 
	 */
	String toString();
}
