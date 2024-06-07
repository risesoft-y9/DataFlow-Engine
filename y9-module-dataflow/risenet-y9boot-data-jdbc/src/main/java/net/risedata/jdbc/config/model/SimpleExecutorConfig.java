package net.risedata.jdbc.config.model;

import java.util.Map;

import net.risedata.jdbc.operation.Operation;

public class SimpleExecutorConfig {
	/**
	 * bean 的config配置
	 */
	private BeanConfig beanConfig;

	/**
	 * 值的map
	 */
	private Map<String, Object> valueMap;
	/**
	 * 传入的对象 可能是string 可能是 class 可能是 entiry
	 */
	private Object data;
	/**
	 * 自定义的操作 map
	 */
	private Map<String, Operation> operationMap;
	/**
	 * 返回类型
	 */
	private Class<?> returnType;


	/**
	 * @param beanConfig
	 * @param valueMap
	 * @param data
	 * @param operationMap
	 * @param returnType
	 */
	public SimpleExecutorConfig(BeanConfig beanConfig, Map<String, Object> valueMap, Object data,
			Map<String, Operation> operationMap, Class<?> returnType) {
		this.beanConfig = beanConfig;
		this.valueMap = valueMap;
		this.data = data;
		this.operationMap = operationMap;
		this.returnType = returnType;
	}

	/**
	 * @return the beanConfig
	 */
	public BeanConfig getBeanConfig() {
		return beanConfig;
	}

	/**
	 * @param beanConfig the beanConfig to set
	 */
	public void setBeanConfig(BeanConfig beanConfig) {
		this.beanConfig = beanConfig;
	}

	

	

	/**
	 * @return the valueMap
	 */
	public Map<String, Object> getValueMap() {
		return valueMap;
	}

	/**
	 * @param valueMap the valueMap to set
	 */
	public void setValueMap(Map<String, Object> valueMap) {
		this.valueMap = valueMap;
	}



	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * @return the operationMap
	 */
	public Map<String, Operation> getOperationMap() {
		return operationMap;
	}

	/**
	 * @param operationMap the operationMap to set
	 */
	public void setOperationMap(Map<String, Operation> operationMap) {
		this.operationMap = operationMap;
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



}
