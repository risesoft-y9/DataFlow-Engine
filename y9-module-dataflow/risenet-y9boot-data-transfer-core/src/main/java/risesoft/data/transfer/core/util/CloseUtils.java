package risesoft.data.transfer.core.util;

import risesoft.data.transfer.core.close.Closed;

/**
 * 关闭操作
 * 
 * @typeName CloseUtils
 * @date 2023年12月8日
 * @author lb
 */
public class CloseUtils {

	public static void close(Closed closed) {
		try {
			closed.close();
		} catch (Exception e) {
		}
	}
}
