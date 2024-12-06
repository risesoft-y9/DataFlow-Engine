package risesoft.data.transfer.core.util;

import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

/**
 * 值操作工具类
 * 
 * @typeName ValueUtils
 * @date 2023年12月8日
 * @author lb
 */
public class ValueUtils {

	public static <T> T getRequired(T value, String message) {
		if (value == null) {
			throw TransferException.as(CommonErrorCode.CONFIG_ERROR, message);
		}
		return value;
	}

	public static <T> T getDefault(T value, T defaultValue) {
		return value == null ? defaultValue : value;
	}

}
