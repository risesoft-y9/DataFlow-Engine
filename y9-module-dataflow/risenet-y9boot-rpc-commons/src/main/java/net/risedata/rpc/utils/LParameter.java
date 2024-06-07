package net.risedata.rpc.utils;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * 对于字段参数的封装
 * @author libo
 *2020年8月21日
 */
public class LParameter{
	private String parameterName;//字段名
	private boolean isPath;
    private Class<?> parameterType;//参数类型
    private Annotation[] annotations;//注解集合
    private boolean required ;
	/**
	 * 泛型 集合
	 */
	private Class<?>[] generalType;
	/**
	 * 是否存在泛型
	 */
	private boolean isGeneral;

	/**
	 * @return the isPath
	 */
	public boolean isPath() {
		return isPath;
	}

	/**
	 * @param isPath the isPath to set
	 */
	public void setPath(boolean isPath) {
		this.isPath = isPath;
	}

	/**
	 * @return the required
	 */
	public boolean isRequired() {
		return required;
	}

	/**
	 * @param required the required to set
	 */
	public void setRequired(boolean required) {
		this.required = required;
	}


	@SuppressWarnings("unchecked")
	public <T extends Annotation>  T getAnnotation(Class<T> annotationClass) {

    	for (int i = 0; i < annotations.length; i++) {
			if (annotationClass==annotations[i].annotationType()) {
				return (T) annotations[i];
			}
		}
    	return null;
    }
    
	/**
	 * @return the annotations
	 */
	public Annotation[] getAnnotations() {
		return annotations;
	}
	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(Annotation[] annotations) {
		this.annotations = annotations;
	}
	/**
	 * @return the parameterName
	 */
	public String getParameterName() {
		return parameterName;
	}
	/**
	 * @param parameterName the parameterName to set
	 */
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	/**
	 * @return the parameterType
	 */
	public Class<?> getParameterType() {
		return parameterType;
	}
	/**
	 * @param parameterType the parameterType to set
	 */
	public void setParameterType(Class<?> parameterType) {
		this.parameterType = parameterType;
	}




	public Class<?>[] getGeneralType() {
		return generalType;
	}

	public void setGeneralType(Class<?>[] generalType) {
		this.generalType = generalType;
	}

	public boolean isGeneral() {
		return isGeneral;
	}

	public void setGeneral(boolean general) {
		isGeneral = general;
	}

	@Override
	public String toString() {
		return "LParameter{" +
				"parameterName='" + parameterName + '\'' +
				", isPath=" + isPath +
				", parameterType=" + parameterType +
				", annotations=" + Arrays.toString(annotations) +
				", required=" + required +
				", generalType=" + Arrays.toString(generalType) +
				", isGeneral=" + isGeneral +
				'}';
	}
}