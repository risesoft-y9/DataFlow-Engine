package net.risedata.jdbc.condition.parse.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.risedata.jdbc.config.model.BeanConfig;
/**
 * 调用里面参数的tostring方法
 * @author libo
 *2020年11月30日
 */
public class ToStringParse  extends BaseParse{
	public static final String rgex = "\\#tos\\{(.*?)\\}";
	public static final Pattern p = Pattern.compile(rgex);
	@Override
	public Pattern getPattern() {
		return p;
	}

	@Override
	public String parse(String group, BeanConfig bc,Matcher m) {
		String content = m.group(1);
		return  content+".toString()";
	}
   
}
