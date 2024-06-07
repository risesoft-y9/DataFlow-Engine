package net.risedata.jdbc.repository.parse.sql.handles;

import java.lang.reflect.Method;

import net.risedata.jdbc.commons.exceptions.ParseException;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.factory.BeanConfigFactory;
import net.risedata.jdbc.repository.model.ArgsBuilder;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.parse.sql.SqlParseHandle;

/**
 * 用于解析表名的执行器 使用方法:#{表名对应参数} 参数可以是?1 ?2 或者
 * 
 * @author libo 2021年7月12日
 */
public class ReplaceParseHandle implements SqlParseHandle {

	@Override
	public boolean isHandle(String instruction) {
		return instruction.startsWith("#{?");
	}

	@Override
	public String parse(Method m, ReturnType returnType, ArgsBuilder argsBuilder, String instruction,
			StringBuilder methodBody) {
		int index = Integer.valueOf(instruction.substring(3, instruction.length() - 1));
		if (index > m.getParameterCount()) {
			throw new ParseException("index " + index + " parameter size = " + m.getParameterCount());
		}
		Class<?> type = m.getParameterTypes()[index - 1];
		BeanConfig bc = BeanConfigFactory.getInstance(type);
		argsBuilder.delete(index - 1);
		if (bc == null) {
			if (type == Class.class) {
				String key = getKey(index);
				if (methodBody.indexOf(key) == -1) {
					methodBody.append("java.lang.String " + key
							+ " =  net.risedata.jdbc.factory.BeanConfigFactory.getTableName($" + index + ");");
				}
				return   '"'+"+" + key +  "+"+'"' ;
			} else {
				return '"' + "+$" + index + "+" + '"';
			}
		} else {
			return bc.getTableName();
		}
	}

	private static String getKey(int index) {
		return "$parameter_" + index;
	}

}
