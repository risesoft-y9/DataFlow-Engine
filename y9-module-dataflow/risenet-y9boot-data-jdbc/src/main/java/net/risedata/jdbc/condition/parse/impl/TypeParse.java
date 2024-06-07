package net.risedata.jdbc.condition.parse.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.search.exception.IfHandleException;
import net.risedata.jdbc.type.parse.TypeParseHandle;
import net.risedata.jdbc.type.parse.impl.BigDecimalTypeParseHandle;
import net.risedata.jdbc.type.parse.impl.ByteTypeParseHandle;
import net.risedata.jdbc.type.parse.impl.DoubleTypeParseHandle;
import net.risedata.jdbc.type.parse.impl.FloatTypeParseHandle;
import net.risedata.jdbc.type.parse.impl.IntTypeParseHandle;
import net.risedata.jdbc.type.parse.impl.LongTypeParseHandle;
import net.risedata.jdbc.type.parse.impl.ShortTypeParseHandle;
/**
 * 类型解析器
 * @author libo
 *2020年11月30日
 */

public class TypeParse  extends BaseParse{
	
	public static final List<TypeParseHandle> TYPES =new ArrayList<TypeParseHandle>();
	static {
		TYPES.add(new DoubleTypeParseHandle());
		TYPES.add(new LongTypeParseHandle());
		TYPES.add(new FloatTypeParseHandle());
		TYPES.add(new IntTypeParseHandle());
		TYPES.add(new ByteTypeParseHandle());
		TYPES.add(new ShortTypeParseHandle());
		TYPES.add(new BigDecimalTypeParseHandle());
	}
	
	public static final String rgex = "\\#type\\{(.*?)\\}";
	public static final Pattern p = Pattern.compile(rgex);
	@Override
	public Pattern getPattern() {
		return p;
	}

	@Override
	public String parse(String group, BeanConfig bc,Matcher m) {
		String fieldName = m.group(1);
		List<String> parameters = getParameter(fieldName);
		if (parameters.size()!=1) {
			throw new IfHandleException("type parameter size != 1 ");
		}
		String type =  getParameterValue(parameters.get(0));
		String value = fieldName.replace(getParameterStr(type),"");
		for (TypeParseHandle tp : TYPES) {
			if (tp.isHandle(type)) {
				return tp.parseValue(value, type);
			}
		}
		throw new  IfHandleException("type "+type+"no handle");
	
	}
    public static TypeParseHandle getParse(String type) {
    	for (TypeParseHandle tp : TYPES) {
			if (tp.isHandle(type)) {
				return tp;
			}
		}
    	return null;
    }
}
