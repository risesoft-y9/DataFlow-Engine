package net.risedata.jdbc.condition.parse.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.risedata.jdbc.condition.parse.Parse;

/**
 * 基础的解析器 提供get参数等公共方法
 * @author libo
 *2020年11月30日
 */
public abstract class BaseParse implements Parse{
	public static final String rgex = "\\$\\<(.*?)\\>";
	public static final Pattern p = Pattern.compile(rgex);

	public static final Pattern  NUMBER = Pattern.compile("^-?\\d+(\\.\\d+)?$");
	
	protected boolean isNumber(String str) {
		return NUMBER.matcher(str).matches();
	}
	
	/**
	 * 返回的是被隔开的字符串
	 * @param expression
	 * @return
	 */
    protected List<String> getParameter(String expression) {
    	Matcher m = p.matcher(expression);
    	List<String> ret= new ArrayList<>();
    	while (m.find()) {
			ret.add(m.group());
		}
    	return ret;
    }
    
    /**
     * 拿到正则表达式里面的值
     * @param expression
     * @return
     */
    protected String getParameterValue(String expression) {
    	Matcher m = p.matcher(expression);
    	return m.find()?m.group(1):null;
    }
    /**
     * 进行正则的包装
     * @param expression
     * @return
     */
    protected String getParameterStr(String expression) {
    	return "$<"+expression+">";
    }
    
}
