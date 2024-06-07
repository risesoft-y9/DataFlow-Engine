package net.risedata.jdbc.factory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.risedata.jdbc.executor.log.PrintExecutor;

/**
 * 打印日志
 * 
 * @author libo 2021年3月24日
 */
public class LoggerFactory {

	@SuppressWarnings("unchecked")
	public static final <T> T getInstance(Object obj, Class<T> interfaceClass, PrintExecutor print) {

		return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
				new Class[] { interfaceClass }, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if (args != null && args.length > 0) {
							print.print(args[0].toString(), args);
						}
						return method.invoke(obj, args);
					}
				});

	}

}
