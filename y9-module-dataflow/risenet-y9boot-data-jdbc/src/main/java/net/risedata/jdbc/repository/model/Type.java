package net.risedata.jdbc.repository.model;

import java.util.List;

/**
 * 封装 类型
 * @author lb
 *
 */
public class Type {

	private Class<?> type;
	
	private Class<?> generalClass;
	
	/**
	 * @param type
	 * @param generalClass
	 */
	public Type(Class<?> type, Class<?> generalClass) {
		this.type = type;
		this.generalClass = generalClass;
	}

	public boolean isList() {
		return List.class.isAssignableFrom(type);
	}
	
	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}
	/**
	 * @return the generalClass
	 */
	public Class<?> getGeneralClass() {
		return generalClass;
	}
	/**
	 * @param generalClass the generalClass to set
	 */
	public void setGeneralClass(Class<?> generalClass) {
		this.generalClass = generalClass;
	}
	
	
}
