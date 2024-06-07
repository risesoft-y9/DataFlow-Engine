package net.risedata.jdbc.repository.parse;

import java.lang.reflect.Method;

import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.model.ReturnType;

/**
 * 解析方法
 * 
 * @author libo 2021年7月8日
 */
public interface MethodParseHandle {
	

	/**
	 * 是否为此处理器处理
	 * 
	 * @param m
	 * @return
	 */
	public boolean isHandle(Method m);

	/**
	 * 解析方法
	 * @param m 方法
	 * @param returnType 返回值
	 * @return 返回code
	 */
	public String parse(Method m, ReturnType returnType,ClassBuild properties);
}
