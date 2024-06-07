package net.risedata.jdbc.repository.parse;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;

import net.risedata.jdbc.exception.InstanceException;
import net.risedata.jdbc.exception.ProxyException;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.model.ReturnType;
import net.risedata.jdbc.repository.model.Type;
import net.risedata.jdbc.utils.GenerityModel;

/**
 * 构建方法返回值的工厂
 * 
 * @author libo 2021年7月8日
 */
public class ReturnTypeFactory {
	/**
	 * 解析方法拿到返回值对象
	 * 
	 * @param m
	 * @return
	 */
	public static ReturnType parseInstance(Method m, ClassBuild methodClass) {

		// 通过这个class 来获取对象

		String genericityName = m.getGenericReturnType().getTypeName();
		Class<?> returnType = m.getReturnType();
		if (returnType == Object.class) {
			if (!genericityName.equals("java.lang.Object")) {
				int index = findT(genericityName, m);
				if (index != -1) {
					return new ReturnType(returnType, null, false, index);
				}
			}
			Class<?> genericityClass = getGenericityClass(m.getGenericReturnType().getTypeName(), m, methodClass);
			if (genericityClass != null) {
				return new ReturnType(genericityClass);
			}
			// TODO 增加获取具体的class
			throw new ProxyException("An undefined type " + genericityName);

		}
		if (!genericityName.contains("java.util.Map")) {
			int start = genericityName.indexOf("<");
			int end = start == -1 ? -1 : genericityName.indexOf(">");
			if (start != -1 && end != -1) {
				genericityName = genericityName.substring(start + 1, end);
				int index = findT(genericityName, m);
				if (index == -1) {
					Class<?> genericityClass;
					try {
						genericityClass = Class.forName(genericityName);
						return new ReturnType(returnType, genericityClass);
					} catch (ClassNotFoundException e) {
						genericityClass = getGenericityClass(genericityName, m, methodClass);
						if (genericityClass != null) {
							return new ReturnType(returnType, genericityClass);
						}
						throw new ProxyException("class not found " + e.getMessage());
					}
				}
				return new ReturnType(returnType, null, false, index);
			}
		} else {
			return new ReturnType(returnType, Map.class);
		}

		return new ReturnType(returnType);
	}

	private static Class<?> getGenericityClass(String name, Method m, ClassBuild methodClass) {
		GenerityModel[] gm = methodClass.getGenerityModels().get(m.getDeclaringClass().getName());
		if (gm != null) {
			for (int i = 0; i < gm.length; i++) {
				GenerityModel generityModel = gm[i];
				if (generityModel.getName().equals(name)) {
					return generityModel.getGenerityClass();
				}
			}

		}
		return null;
	}

	/**
	 * 拿到泛型T 对应class
	 * 
	 * @param genericityName
	 * @param m
	 * @return
	 */
	private static int findT(String genericityName, Method m) {
		Parameter[] ps = m.getParameters();
		for (int i = 0; i < ps.length; i++) {
			if (ps[i].getParameterizedType().getTypeName().contains("<" + genericityName + ">")) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 解析为类型不支持泛型
	 * 
	 * @param sourceName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Type parseType(String sourceName) {
		try {
			int startIndex = sourceName.indexOf("<");
			if (startIndex != -1) {
				int endIndex = sourceName.lastIndexOf(">");
				if (endIndex != -1) {
					String generalClassName = sourceName.substring(startIndex + 1, endIndex);
					String className = sourceName.substring(0, startIndex);

					return new Type(Thread.currentThread().getContextClassLoader().loadClass(className),
							Thread.currentThread().getContextClassLoader().loadClass(generalClassName));
				}
			} else {
				return new Type(Thread.currentThread().getContextClassLoader().loadClass(sourceName), null);
			}
		} catch (Exception e) {
			throw new InstanceException(e.getMessage() + " 无法解析sourceName " + sourceName);
		}
		throw new InstanceException("无法解析sourceName " + sourceName);
	}
}
