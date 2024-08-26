package net.risesoft.util;

import java.util.List;
import java.util.Map;

/**
 * 数据处理工具类
 * @author pzx
 *
 */
public class DataUtils {
	
	/**
	 * 检验数据是什么类型
	 * @param value
	 * @return
	 */
	public static String checkType(Object value) {
        if (value instanceof Integer) {
            return "Integer";
        } else if (value instanceof String) {
            return "String";
        } else if (value instanceof Double) {
        	return "Double";
        } else if (value instanceof Float) {
        	return "Float";
        } else if (value instanceof Long) {
        	return "Long";
        } else if (value instanceof Map) {
        	return "Map";
        } else if (value instanceof List) {
        	return "List";
        } else if (value instanceof Boolean) {
        	return "Boolean";
        } else {
        	return "";
        }
	}
}
