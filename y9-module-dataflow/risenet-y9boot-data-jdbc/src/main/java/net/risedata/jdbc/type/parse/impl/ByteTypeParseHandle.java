package net.risedata.jdbc.type.parse.impl;

import net.risedata.jdbc.type.parse.TypeParseHandle;

/**
 * 处理 int 类型
 * @author libo
 *2020年11月30日
 */
public class ByteTypeParseHandle implements TypeParseHandle{

	@Override
	public boolean isHandle(String type) {
		
		return "byte".equals(type)||"java.lang.Byte".equals(type);
	}

	@Override
	public String parseValue(String value, String type) {
		return "Byte.valueOf("+value+")";
	}
    
}
