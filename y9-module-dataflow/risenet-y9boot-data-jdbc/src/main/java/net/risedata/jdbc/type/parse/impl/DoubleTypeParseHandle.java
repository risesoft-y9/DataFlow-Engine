package net.risedata.jdbc.type.parse.impl;

import net.risedata.jdbc.type.parse.TypeParseHandle;

/**
 * 处理 int 类型
 * @author libo
 *2020年11月30日
 */
public class DoubleTypeParseHandle implements TypeParseHandle{

	@Override
	public boolean isHandle(String type) {
		return "double".equals(type)||"java.lang.Double".equals(type);
	}

	@Override
	public String parseValue(String value, String type) {
		return "Double.valueOf("+value+")";
	}
    
}
