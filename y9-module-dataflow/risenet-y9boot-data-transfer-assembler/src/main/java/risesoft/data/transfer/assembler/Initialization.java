package risesoft.data.transfer.assembler;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import risesoft.data.transfer.core.exception.InstallException;

import java.util.Set;

/**
 * 初始化工具
 * 
 * 
 * @typeName Initialization
 * @date 2024年8月29日
 * @author lb
 */
public class Initialization {

	private static final Logger LOGGER = LoggerFactory.getLogger(Initialization.class);

	/**
	 * 初始化:初始化有生公司包自带的实现类
	 * 
	 * @param interfaceClass 需要初始化的类型
	 */
	public static int install(Class<?> interfaceClass) {
		return installOfPackage(interfaceClass, "risesoft.data.transfer");
	}

	/**
	 * 根据包路径与接口加载类
	 * 
	 * @param interfClass
	 * @param packageName
	 * @return
	 */
	public static int installOfPackage(Class<?> interfaceClass, String packageName) {
		Reflections reflections = new Reflections(packageName);
		Set<?> subTypes = reflections.getSubTypesOf(interfaceClass);
		for (Object clazz : subTypes) {
			try {
				LOGGER.info("install: " + ((Class) clazz).getName());
				Class.forName(((Class) clazz).getName());
			} catch (Exception e) {
				throw new InstallException("加载类:" + clazz + "失败!" + e.getMessage());
			}
		}
		return subTypes.size();
	}

}