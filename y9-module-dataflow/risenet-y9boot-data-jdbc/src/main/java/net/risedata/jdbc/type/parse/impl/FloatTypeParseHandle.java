package net.risedata.jdbc.type.parse.impl;

import net.risedata.jdbc.type.parse.TypeParseHandle;

/**
 * 处理 int 类型
 * @author libo
 *2020年11月30日
 */
public class FloatTypeParseHandle implements TypeParseHandle{

	@Override
	public boolean isHandle(String type) {
		return "float".equals(type)||"java.lang.Float".equals(type);
	}

	@Override
	public String parseValue(String value, String type) {
		return "Float.valueOf("+value+")";
	}
    
}
