package net.risedata.jdbc.condition.parse.impl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.search.exception.IfHandleException;
import net.risedata.jdbc.type.parse.TypeParseHandle;
/**
 * 相等的解析器 调用的 equals方法
 * @author libo
 *2020年11月30日
 */
public class EQParse  extends BaseParse{
	public static final String rgex = "\\#eq\\{(.*?)\\}";
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
		if(isNumber(p1)) {//数字类型==
			//通过getValue解析出来的解析器
			String type = GetValueParse.getType(p2);
			if (type!=null) {
				TypeParseHandle tph = TypeParse.getParse(type);
				if (tph!=null) {
					return  tph.parseValue(p1, type)+" == "+p2;
				}
			}
		};

		return  p1+".equals("+p2+")";
	}

}
