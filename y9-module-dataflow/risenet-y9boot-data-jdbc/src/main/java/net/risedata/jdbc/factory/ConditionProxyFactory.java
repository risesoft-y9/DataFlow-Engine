package net.risedata.jdbc.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.risedata.jdbc.condition.Condition;
import net.risedata.jdbc.condition.parse.Parse;
import net.risedata.jdbc.condition.parse.impl.EQParse;
import net.risedata.jdbc.condition.parse.impl.GetValueParse;
import net.risedata.jdbc.condition.parse.impl.LikeParse;
import net.risedata.jdbc.condition.parse.impl.StringParse;
import net.risedata.jdbc.condition.parse.impl.ToStringParse;
import net.risedata.jdbc.condition.parse.impl.TypeParse;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.search.exception.IfHandleException;

/**
 * if 条件执行器接口的 工厂类
 * 
 * @author libo 2020年10月20日
 */

public class ConditionProxyFactory {
	private static final List<Parse> PARSES = new ArrayList<Parse>();
	
	private static final Random random = new Random();

	static {// 代理解析标签的对象
		PARSES.add(new GetValueParse());
		PARSES.add(new EQParse());
		PARSES.add(new TypeParse());
		PARSES.add(new ToStringParse());
		PARSES.add(new StringParse());
		PARSES.add(new LikeParse());
		ClassPool.getDefault().insertClassPath(new ClassClassPath(Condition.class));
	}

	/**
	 * 根据表达式拿到对应的判断对象
	 * 
	 * @param expression
	 * @param beanClass  被解析的对象
	 * @return
	 */
	public static Condition getInstance(String expression, Class<?> beanClass) {
		String pare = parseIf(expression, beanClass);
		try {
			return newProxyInstance(Thread.currentThread().getContextClassLoader(), Condition.class, pare);
		} catch (Throwable e) {
			throw new IfHandleException(expression + "parse later " + pare + " expression error please check it "
					+ e.getMessage() + " type :" + e);

		}
	}

	/**
	 * 解析条件产生code
	 * 
	 * @param expression 表达式
	 * @param beanClass  对应的接口
	 * @return
	 */
	public static String parseIf(String expression, Class<?> beanClass) {

		StringBuilder parse = new StringBuilder("java.util.Map $value = (java.util.Map)$args[0];");
		BeanConfig bc = BeanConfigFactory.getInstance(beanClass);
		StringBuilder newexpression = new StringBuilder(expression);
		Matcher m;
		String group;
		String temp;
		for (Parse p : PARSES) {
			m = p.getPattern().matcher(newexpression);
			while (m.find()) {
				group = m.group();
				temp = p.parse(group, bc, m);
				newexpression.replace(m.start(), m.end(), temp);
				m.reset(newexpression);
			}
		}
		parse.append("return " + newexpression + ";");
		return parse.toString();
	}

	/**
	 * 为接口创建实现类
	 * 
	 * @param <T>
	 * @param loader         类加载器
	 * @param interfaceClass 接口
	 * @param methodBody     方法body
	 * @return
	 * @throws Throwable
	 */
	@SuppressWarnings({ "unchecked" })
	private static <T> T newProxyInstance(ClassLoader loader, Class<T> interfaceClass, String methodBody)
			throws Throwable {
		ClassPool pool = ClassPool.getDefault();
		CtClass proxyCc = pool.makeClass("LProxy$" + random.nextInt() + "@" + random.nextInt());
		CtClass interfaceCc = pool.get(interfaceClass.getName());
		proxyCc.addInterface(interfaceCc);
		CtMethod[] ctMethods = interfaceCc.getDeclaredMethods();
		for (int i = 0; i < ctMethods.length; i++) {
			CtMethod newMethod = new CtMethod(ctMethods[i].getReturnType(), ctMethods[i].getName(),
					ctMethods[i].getParameterTypes(), proxyCc);

			newMethod.setBody("{" + methodBody + "}");
			proxyCc.addMethod(newMethod);
		}
		Object proxy = proxyCc.toClass().getConstructor().newInstance();
		return (T) proxy;
	}

}
