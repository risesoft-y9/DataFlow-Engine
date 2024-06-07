package net.risedata.jdbc.repository.parse.sql;

import java.lang.reflect.Method;

import net.risedata.jdbc.repository.model.ArgsBuilder;
import net.risedata.jdbc.repository.model.ReturnType;

public interface SqlParseHandle {

	boolean isHandle(String instruction);

	String parse(Method m, ReturnType returnType, ArgsBuilder argsBuilder, String instruction,StringBuilder methodBody);
}
