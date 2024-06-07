package net.risedata.jdbc.repository.parse.handles;

import java.lang.reflect.Method;
import org.springframework.core.annotation.AnnotationUtils;

import net.risedata.jdbc.annotations.repository.Modify;
import net.risedata.jdbc.commons.exceptions.ParseException;
import net.risedata.jdbc.repository.model.ArgsBuilder;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.parse.MethodParseHandle;
import net.risedata.jdbc.repository.parse.sql.SqlParseFactory;
import net.risedata.jdbc.repository.proxy.RepositoryCreateFactory;

public class UpdateParseHandle implements MethodParseHandle {

	@Override
	public boolean isHandle(Method m) {
		return AnnotationUtils.findAnnotation(m, Modify.class) != null;
	}

	@Override
	public String parse(Method m, ReturnType returnType, ClassBuild properties) {
		Modify modify = AnnotationUtils.findAnnotation(m, Modify.class);
		if (returnType.isVoid() || returnType.getReturnType() == int.class
				|| returnType.getReturnType() == Integer.class) {
			return parseBody(m, returnType, properties, modify.value());
		} else {
			throw new ParseException("modify return type unidentifiable " + m);
		}
	}

	public static String parseBody(Method m, ReturnType returnType, ClassBuild properties, String sql) {
		ArgsBuilder args = new ArgsBuilder(m);
		StringBuilder body = new StringBuilder(SqlParseFactory.parseSql(returnType, m, args, sql));
		body.append(args.toBody());
		body.append(toReturn(returnType, args));
		return body.toString();
	}

	private static String toReturn(ReturnType type, ArgsBuilder argsBuilder) {
		return (type.isVoid() ? "" : type.getReturnType() == int.class ? "return ($r)" : "return ")
				+ RepositoryCreateFactory.JDBC_TEMPLATE + ".update(_sql" + argsBuilder.to() + ");";
	}

}
