package net.risedata.jdbc.condition.parse.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.search.exception.IfHandleException;
/**
 * 调用indexof方法接收2个参数第一个为调用参数 
 * 如果第一个参数没有被tostring或者没有被"包裹起来 则会调用tostring再indexOf
 * @author libo
 *2020年11月30日
 */
public class LikeParse  extends BaseParse{
	public static final String rgex = "\\#like\\{(.*?)\\}";
	public static final Pattern p = Pattern.compile(rgex);
	@Override
	public Pattern getPattern() {
		return p;
	}

	@Override
	public String parse(String group, BeanConfig bc,Matcher m) {
		String fieldName = m.group(1);
	
		List<String> parameters = getParameter(fieldName);
		if (parameters.size()!=2) {
			throw new IfHandleException(" eq parameter size != 2 ");
		}
		String p1 =  getParameterValue(parameters.get(0));
		String p2 = getParameterValue(parameters.get(1));
		return '('+StringParse.getStringOrToString(p1)+".indexOf("+StringParse.getStringOrToString(p2)+") != -1)";
		
	}
  
}
