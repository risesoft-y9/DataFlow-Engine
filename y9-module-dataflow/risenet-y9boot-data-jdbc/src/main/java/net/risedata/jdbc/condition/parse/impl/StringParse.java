package net.risedata.jdbc.condition.parse.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.risedata.jdbc.config.model.BeanConfig;
/**
 * 将字符串左右添加上"号
 * @author libo
 *2020年11月30日
 */
public class StringParse  extends BaseParse{
	public static final String rgex = "\\#s\\{(.*?)\\}";
	public static final Pattern p = Pattern.compile(rgex);
	@Override
	public Pattern getPattern() {
		return p;
	}

	@Override
	public String parse(String group, BeanConfig bc,Matcher m) {
		String content = m.group(1);
		return  '"'+content+'"';
	}
	/**
	 * 判断一个字符串是否为string
	 * @param str
	 * @return
	 */
	public static boolean isString(String str) {
	    return str.charAt(0) == '"' && str.charAt(str.length()-1) == '"';
	}
	/**
	 * 拿到一个字符串的string值如果不是string对象会调用toString
	 * @param str
	 * @return
	 */
	public static String getStringOrToString(String str) {
		return isString(str)?str:str+".toString()";
	}
	
}
