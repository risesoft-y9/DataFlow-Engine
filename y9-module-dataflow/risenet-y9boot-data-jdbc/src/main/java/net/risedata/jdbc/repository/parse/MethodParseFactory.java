package net.risedata.jdbc.repository.parse;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.parse.handles.MethodNameParseHandle;
import net.risedata.jdbc.repository.parse.handles.SearchParseHandle;
import net.risedata.jdbc.repository.parse.handles.UpdateParseHandle;

/**
 * 解析方法
 * 
 * @author libo 2021年7月8日
 */
public class MethodParseFactory {

	private static List<MethodParseHandle> HANDLES = new ArrayList<>();
	static {
		HANDLES.add(new SearchParseHandle());
		HANDLES.add(new UpdateParseHandle());
		HANDLES.add(new MethodNameParseHandle());
	}

	/**
	 * 解析一个方法返回code
	 * 
	 * @param m
	 * @return
	 */
	public static String parseMethod(Method m, ClassBuild properties) {
		ReturnType returnType = ReturnTypeFactory.parseInstance(m, properties);
		for (MethodParseHandle methodParseHandle : HANDLES) {
			if (methodParseHandle.isHandle(m)) {
				return methodParseHandle.parse(m, returnType, properties);
			}
		}
		return null;
	}
}
