package net.risedata.jdbc.repository.model;

import java.util.HashMap;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import net.risedata.jdbc.exception.ProxyException;
import net.risedata.jdbc.utils.GenerityModel;

/**
 * 用于创建class 提供更多的class 信息
 * 
 * @author libo 2021年7月8日
 */
public class ClassBuild {

	private ClassPool pool;
	private CtClass proxy;
	private Map<String, GenerityModel[]> generityModels;

	/**
	 * 来源class
	 */
	private Class<?> sourceClass;
	/**
	 * 泛型class
	 */
	private Class<?> genericityClass;

	/**
	 * @param pool
	 * @param proxy
	 */
	public ClassBuild(ClassPool pool, CtClass proxy, Class<?> sourceClass, Class<?> genericityClass,
			Map<String, GenerityModel[]> generityModels) {
		this.pool = pool;
		this.proxy = proxy;
		this.sourceClass = sourceClass;
		this.genericityClass = genericityClass;
		this.generityModels = generityModels;
	}

	/**
	 * @return the generityModels
	 */
	public Map<String, GenerityModel[]> getGenerityModels() {
		return generityModels;
	}

	private Map<String, Object> valueMap = new HashMap<String, Object>();

	/**
	 * 添加一个字段并且添加值
	 * 
	 * @param fieldName
	 * @param value
	 * @return
	 */
	public ClassBuild addField(String fieldName, Object value) {
		addField(fieldName, value.getClass());
		putValue(fieldName, value);
		return this;
	}

	/**
	 * @return the sourceClass
	 */
	public Class<?> getSourceClass() {
		return sourceClass;
	}

	/**
	 * @return the genericityClass
	 */
	public Class<?> getGenericityClass() {
		return genericityClass;
	}

	/**
	 * 添加一个属性的值
	 * 
	 * @param fieldName
	 * @param value
	 */
	public void putValue(String fieldName, Object value) {
		valueMap.put(fieldName, value);
	}

	/**
	 * 添加一个字段
	 * 
	 * @param fieldName 名字
	 * @param type      类型
	 */
	public void addField(String fieldName, Class<?> type) {
		CtClass jtType;
		try {
			jtType = pool.getCtClass(type.getName());
			CtField field = new CtField(jtType, fieldName, proxy);
			field.setModifiers(Modifier.PUBLIC);
			proxy.addField(field);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ProxyException(e.getMessage());
		}

	}

	/**
	 * 创建之后的回调
	 * 
	 * @param instance
	 */
	public void createed(Object instance) {
		valueMap.forEach((k, v) -> {
			try {
				instance.getClass().getField(k).set(instance, v);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * @return the pool
	 */
	public ClassPool getPool() {
		return pool;
	}

	/**
	 * @param pool the pool to set
	 */
	public void setPool(ClassPool pool) {
		this.pool = pool;
	}

	/**
	 * @return the proxy
	 */
	public CtClass getProxy() {
		return proxy;
	}

	/**
	 * @param proxy the proxy to set
	 */
	public void setProxy(CtClass proxy) {
		this.proxy = proxy;
	}

	public boolean hasField(String mappingName) {
		return valueMap.containsKey(mappingName);
	}

}
