package net.risedata.jdbc.repository.parse.handles;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;

import net.risedata.jdbc.annotations.repository.Search;
import net.risedata.jdbc.commons.exceptions.ParseException;
import net.risedata.jdbc.repository.model.ArgsBuilder;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.parse.MethodParseHandle;
import net.risedata.jdbc.repository.parse.handles.search.DefaultSearchReturnHandle;
import net.risedata.jdbc.repository.parse.handles.search.ListSearchReturnHandle;
import net.risedata.jdbc.repository.parse.handles.search.MapSearchReturnHandle;
import net.risedata.jdbc.repository.parse.handles.search.SearchReturnHandle;
import net.risedata.jdbc.repository.parse.sql.SqlParseFactory;

public class SearchParseHandle implements MethodParseHandle {

	private static List<SearchReturnHandle> HANDLES = new ArrayList<SearchReturnHandle>();
	static {
		HANDLES.add(new ListSearchReturnHandle());
		HANDLES.add(new MapSearchReturnHandle());
		HANDLES.add(new DefaultSearchReturnHandle());
	}

	@Override
	public boolean isHandle(Method m) {
		return AnnotationUtils.findAnnotation(m, Search.class) != null;
	}

	@Override
	public String parse(Method m, ReturnType returnType, ClassBuild properties) {
		Search search = AnnotationUtils.findAnnotation(m, Search.class);
		if (returnType.isVoid()) {
			throw new ParseException("search " + m.getName() + " returnType is null ");
		}

		return parseBody(m, returnType, properties, search.value());

	}

	public static String parseBody(Method m, ReturnType returnType, ClassBuild properties, String sql) {
		ArgsBuilder args = new ArgsBuilder(m);
		StringBuilder body = new StringBuilder(SqlParseFactory.parseSql(returnType, m, args, sql));
		body.append(args.toBody());
		body.append(parseReturnValue(returnType, properties, args));
		return body.toString();
	}

	private static String parseReturnValue(ReturnType type, ClassBuild properties, ArgsBuilder argsBuilder) {
		for (SearchReturnHandle searchReturnHandle : HANDLES) {
			if (searchReturnHandle.isHandle(type)) {
				return searchReturnHandle.parse(type, properties, argsBuilder);
			}
		}
		throw new ParseException("search you return type " + type.getReturnType() + " is unidentifiable");
	}

}
