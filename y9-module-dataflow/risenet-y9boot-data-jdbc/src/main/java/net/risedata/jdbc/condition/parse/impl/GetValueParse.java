package net.risedata.jdbc.condition.parse.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.risedata.jdbc.condition.parse.Parse;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.executor.jdbc.JDBC;

public class GetValueParse implements Parse {
	public static final String rgex = "\\#\\{(.*?)\\}";
	public static final Pattern p = Pattern.compile(rgex);
	private static final Map<String, Class<?>> KEY_TYPE_MAPPING = new HashMap<String, Class<?>>();
   static {
	   KEY_TYPE_MAPPING.put(JDBC.SQL_KEY, StringBuilder.class);
   }
	@Override
	public Pattern getPattern() {
		return p;
	}

	@Override
	public String parse(String group, BeanConfig bc, Matcher m) {
		String fieldName = m.group(1);
		FieldConfig fc = bc.getField(fieldName);
		if (fc != null) {
			return "((" + fc.getValueField().getType().getName() + ")$value.get(\"" + fieldName + "\"))";
		} else {
			Class<?> typeClass = KEY_TYPE_MAPPING.get(fieldName);
			if (typeClass!=null) {
				return "((" +typeClass.getName() + ")$value.get(\"" + fieldName + "\"))";
			}
			return "$value.get(\"" + fieldName + "\")";
		}
	}

	/**
	 * 拿到此解析器解析的类型
	 * 
	 * @return
	 */
	public static String getType(String str) {
		if (hasCast(str)) {
			return str.substring(2, str.indexOf(")"));
		}
		return null;
	}

	/**
	 * 是否为此对象强转出来的string
	 * 
	 * @param str
	 * @return
	 */
	public static boolean hasCast(String str) {
		return str.charAt(0) == '(' && str.charAt(str.length() - 1) == ')' && str.charAt(1) == '(';
	}
}
