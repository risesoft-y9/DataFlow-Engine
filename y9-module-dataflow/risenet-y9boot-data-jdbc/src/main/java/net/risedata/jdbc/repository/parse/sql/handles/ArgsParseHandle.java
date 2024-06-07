package net.risedata.jdbc.repository.parse.sql.handles;

import java.lang.reflect.Method;
import java.util.Collection;

import net.risedata.jdbc.commons.exceptions.ParseException;
import net.risedata.jdbc.repository.model.ArgsBuilder;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.parse.sql.SqlParseHandle;

public class ArgsParseHandle implements SqlParseHandle {

	@Override
	public boolean isHandle(String instruction) {
		return instruction.startsWith("?");
	}

	@Override
	public String parse(Method m, ReturnType returnType, ArgsBuilder argsBuilder, String instruction,
			StringBuilder methodBody) {

		Integer index = Integer.valueOf(instruction.substring(1, instruction.length()));
		if (index > m.getParameterCount()) {
			throw new ParseException("index " + index + " parameter size = " + m.getParameterCount());
		}
		
		Class<?> type = m.getParameterTypes()[index-1];
		if (type.isArray()) {
			argsBuilder.appendToBody("for(int i = 0; i<$" + index + ".length;i++){");
			argsBuilder.addArg("$" + index + "[i]");
			argsBuilder.appendToBody("}");
			return '"' + "+net.risedata.jdbc.repository.parse.sql.handles.ArgsParseHandle.getArgsPlaceholder($" + index + ")+"
					+ '"';
		} else if (Collection.class.isAssignableFrom(type)) {
			argsBuilder.appendAllArg("$" + index);
			return '"' + "+net.risedata.jdbc.repository.parse.sql.handles.ArgsParseHandle.getArgsPlaceholder($" + index + ")+"
					+ '"';
		} else {
			argsBuilder.add("$" + index);
		}

		return "?";

	}

	public static String getArgsPlaceholder(Object[] args) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			sb.append("?");
			if (i != args.length - 1) {
				sb.append(",");
			}
		}
		return sb.toString();

	}

	public static String getArgsPlaceholder(Collection<Object> args) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < args.size(); i++) {
			sb.append("?");
			if (i != args.size() - 1) {
				sb.append(",");
			}
		}
		return sb.toString();
	}



}
