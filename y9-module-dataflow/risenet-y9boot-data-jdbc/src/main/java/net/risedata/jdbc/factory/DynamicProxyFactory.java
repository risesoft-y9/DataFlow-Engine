package net.risedata.jdbc.factory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javassist.CtClass;
import javassist.CtMethod;
import net.risedata.jdbc.commons.exceptions.ParseException;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.repository.parse.MethodParseFactory;
import net.risedata.jdbc.repository.proxy.RepositoryCreateFactory;
import net.risedata.jdbc.utils.ClassUtils;

/**
 * 用来动态生成dynamicDao 的实现
 * 
 * @author libo 2021年7月8日
 */
public class DynamicProxyFactory {

	public static <T> T getInstance(Class<T> type) {
		try {
			return newProxyInstance(type);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param <T>
	 * @param loader
	 * @param interfaceClass
	 * @return
	 * @throws Throwable
	 */
	@SuppressWarnings({ "unchecked" })
	private static <T> T newProxyInstance(Class<T> interfaceClass) throws Throwable {
		ClassBuild proxy = RepositoryCreateFactory.create(interfaceClass);
		// 这里需要添加多个接口实现
		List<Class<?>> interfaces = ClassUtils.getInterfaces(interfaceClass);

		CtClass interfaceCc = proxy.getPool().get(interfaceClass.getName());
		CtClass tempClass;
		List<CtMethod> ctMethods = new ArrayList<CtMethod>();
		for (Class<?> class1 : interfaces) {
			tempClass = proxy.getPool().get(class1.getName());
			proxy.getProxy().addInterface(tempClass);
			for (CtMethod method : tempClass.getDeclaredMethods()) {
				ctMethods.add(method);
			}
		}
		proxy.getProxy().addInterface(interfaceCc);
		Method[] methods = interfaceClass.getMethods();
		ctMethods.addAll(Arrays.asList(interfaceCc.getDeclaredMethods()));
		String codeBody;
		Map<String, String> mothodBodyMap = new HashMap<String, String>();
		for (int j = 0; j < methods.length; j++) {
			codeBody = MethodParseFactory.parseMethod(methods[j], proxy);
			if (codeBody == null) {
				throw new ParseException(methods[j] + " no parse");
			}
			mothodBodyMap.put(methods[j].getName() + methods[j].getParameterCount(), codeBody);
		}
		for (int i = 0; i < ctMethods.size(); i++) {
			CtMethod newMethod = new CtMethod(ctMethods.get(i).getReturnType(), ctMethods.get(i).getName(),
					ctMethods.get(i).getParameterTypes(), proxy.getProxy());
		
			newMethod.setBody(
					"{ " + mothodBodyMap.get(newMethod.getName() + ctMethods.get(i).getParameterTypes().length) + " }");

			proxy.getProxy().addMethod(newMethod);
		}
		Object instance = proxy.getProxy().toClass().getConstructor().newInstance();
		RepositoryCreateFactory.instanceed(instance, proxy);
		return (T) instance;
	}

}
