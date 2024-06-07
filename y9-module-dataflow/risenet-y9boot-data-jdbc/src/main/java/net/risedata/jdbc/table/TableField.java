package net.risedata.jdbc.table;
/**
 * 表字段对象
 * @author libo
 *2020年12月10日
 */
public class TableField {
	/**
	 * 是否为key
	 */
    private boolean key;
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 默认值
     */
    private String defaultExpression;
    /**
     * 类型
     */
    private String type;
    /**
     * 是否是必须的
     */
    private boolean required;
    /**
     * 注释
     */
    private String annotation;
	/**
	 * @return the key
	 */
	public boolean isKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 */
	public void setKey(boolean key) {
		this.key = key;
	}
	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return the defaultExpression
	 */
	public String getDefaultExpression() {
		return defaultExpression;
	}
	/**
	 * @param defaultExpression the defaultExpression to set
	 */
	public void setDefaultExpression(String defaultExpression) {
		this.defaultExpression = defaultExpression;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
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
	
	
	/**
	 * @return the annotation
	 */
	public String getAnnotation() {
		return annotation;
	}
	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}
	@Override
	public String toString() {
		return "TableField [key=" + key + ", fieldName=" + fieldName + ", defaultExpression=" + defaultExpression
				+ ", type=" + type + ", required=" + required + ", annotation=" + annotation + "]";
	}
    
    
    
    
}
