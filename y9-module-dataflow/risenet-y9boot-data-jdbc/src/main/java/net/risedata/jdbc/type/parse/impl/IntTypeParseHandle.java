package net.risedata.jdbc.type.parse.impl;

import net.risedata.jdbc.type.parse.TypeParseHandle;

/**
 * 处理 int 类型
 * @author libo
 *2020年11月30日
 */
public class IntTypeParseHandle implements TypeParseHandle{

	@Override
	public boolean isHandle(String type) {
		return "int".equals(type)||"java.lang.Integer".equals(type)||"Integer".equals(type);
	}

	@Override
	public String parseValue(String value, String type) {
		return "Integer.valueOf("+value+")";
	}
    
}
