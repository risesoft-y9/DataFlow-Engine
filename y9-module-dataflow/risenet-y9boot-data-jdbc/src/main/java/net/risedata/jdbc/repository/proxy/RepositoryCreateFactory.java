package net.risedata.jdbc.repository.proxy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javassist.ClassClassPath;
import javassist.ClassPool;
import net.risedata.jdbc.exception.ProxyException;
import net.risedata.jdbc.executor.jdbc.JdbcExecutor;
import net.risedata.jdbc.factory.InstanceFactoryManager;
import net.risedata.jdbc.factory.impl.SpringApplicationFactory;
import net.risedata.jdbc.repository.Repository;
import net.risedata.jdbc.repository.model.ClassBuild;
import net.risedata.jdbc.utils.ClassUtils;

/**
 * 初始化 一些基本的属性在动态dao上
 * 
 * @author libo 2021年7月8日
 */
public class RepositoryCreateFactory {
	/**
	 * 操作jdbcTemplate的类
	 */
	public static final String JDBC_TEMPLATE = "$jt";
	private static final Random r = new Random();

	public static ClassBuild create(Class<?> sourceClass) {
		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ClassClassPath(JdbcExecutor.class));
		ClassBuild properties;
		try {
			// TODO 保留 他的状态
			properties = new ClassBuild(pool, pool.makeClass("LDynamicProxy$" + r.nextInt() + "@" + r.nextInt()),
					sourceClass, getGenerityClass(sourceClass), ClassUtils.getGenerityClasss(sourceClass));
			init(properties);
			return properties;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ProxyException(" create ClassBuilder error" + e.getMessage());
		}
	}

	private static Class<?> getGenerityClass(Class<?> sourceClass) throws ClassNotFoundException {
		if (Repository.class.isAssignableFrom(sourceClass)) {
			Type types = sourceClass.getGenericInterfaces()[0];
			if (types instanceof ParameterizedType) {
				Type genericType = ((ParameterizedType) types).getActualTypeArguments()[0];
				return Thread.currentThread().getContextClassLoader().loadClass(genericType.getTypeName());
			} else {
				return Object.class;
			}

		} else {
			Class<?> generityClass = null;
			for (Class<?> interfaceItem : sourceClass.getInterfaces()) {
				if (Repository.class.isAssignableFrom(interfaceItem)) {
					generityClass = getGenerityClass(interfaceItem);
					break;
				}
			}
			return generityClass;
		}

	}

	/**
	 * 初始化
	 * 
	 * @param ct
	 * @param pool
	 */
	public static void init(ClassBuild classBuild) {

		classBuild.addField(JDBC_TEMPLATE, JdbcExecutor.class);
	}

	private static Map<Object, ClassBuild> instanceMap = new HashMap<Object, ClassBuild>();
	private static boolean isInitEd = false;

	public static void instanceed(Object proxy, ClassBuild classBuild) {
		instanceMap.put(proxy, classBuild);

	}

	public static void startWeb(ApplicationContext ac) {
		InstanceFactoryManager.init(new SpringApplicationFactory(ac));
		if (instanceMap == null || instanceMap.size() <= 0) {
			return;
		}
		if (isInitEd) {
			return;
		} else {
			isInitEd = true;
		}
		instanceMap.forEach((proxy, builder) -> {
			builder.putValue(JDBC_TEMPLATE, ac.getBean(JdbcExecutor.class));
			builder.createed(proxy);

			builder.getProxy().detach();
		});

	}

	@Autowired
	ApplicationContext ac;

	@PostConstruct
	public void Initializing() {
		startWeb(ac);
	}

}
