package net.risedata.jdbc.repository.model;

public class ReturnType {
	/**
	 * 返回值
	 */
	private Class<?> returnType;
	/**
	 * 返回值泛型的类
	 */
	private Class<?> genericityClass;

	/**
	 * 泛型是否为class
	 */
	private boolean isClass;
	/**
	 * 当为泛型的时候index 为args 的位置
	 */
	private int index;
	/**
	 * 是否存在泛型
	 */
	private boolean isGenericity;

	/**
	 * @param returnType
	 * @param genericityClass
	 */
	public ReturnType(Class<?> returnType, Class<?> genericityClass) {
		this.returnType = returnType;
		this.genericityClass = genericityClass;
		this.isClass = true;
		this.isGenericity = true;
	}

	public boolean isVoid() {
		return "void".equals(returnType.getName()) || Void.class == returnType;
	}

	/**
	 * @param returnType
	 * @param genericityClass
	 * @param isClass
	 * @param index
	 */
	public ReturnType(Class<?> returnType, Class<?> genericityClass, boolean isClass, int index) {
		this.returnType = returnType;
		this.genericityClass = genericityClass;
		this.isClass = isClass;
		this.index = index;
		this.isGenericity = true;
	}

	/**
	 * @param returnType
	 */
	public ReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the returnType
	 */
	public Class<?> getReturnType() {
		return returnType;
	}

	/**
	 * @param returnType the returnType to set
	 */
	public void setReturnType(Class<?> returnType) {
		this.returnType = returnType;
	}

	/**
	 * @return the genericityClass
	 */
	public Class<?> getGenericityClass() {
		return genericityClass;
	}

	/**
	 * @param genericityClass the genericityClass to set
	 */
	public void setGenericityClass(Class<?> genericityClass) {
		this.genericityClass = genericityClass;
	}

	/**
	 * @return the isClass
	 */
	public boolean isClass() {
		return isClass;
	}

	/**
	 * @param isClass the isClass to set
	 */
	public void setClass(boolean isClass) {
		this.isClass = isClass;
	}

	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the isGenericity
	 */
	public boolean isGenericity() {
		return isGenericity;
	}

	/**
	 * @param isGenericity the isGenericity to set
	 */
	public void setGenericity(boolean isGenericity) {
		this.isGenericity = isGenericity;
	}

	@Override
	public String toString() {
		return "ReturnType [returnType=" + returnType + ", genericityClass=" + genericityClass + ", isClass=" + isClass
				+ ", index=" + index + "]";
	}

}
