package net.risedata.jdbc.executor.search.model;

import java.util.Map;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.SimpleExecutorConfig;
import net.risedata.jdbc.mapping.LRowMapping;
import net.risedata.jdbc.operation.Operation;
/**
 * 查询的配置类  简化传参的数量 使用 class的形式  
 * TODO 有待争议是否使用此方式
 * @author libo
 * 2021年2月10日
 * @param <T>
 */
public class SearchConfig<T> extends SimpleExecutorConfig {

	
	
	/**
	 * 是否使用排序
	 */
	private boolean isOrder = true;
	/**
	 * 是否加载@transient的注解的字段进行映射
	 */
	private boolean isTransient = true;

	/**
	 * 自定义的返回类型
	 */
	LRowMapping<T> rowmapping;

	
	
	


	/**
	 * @param beanConfig
	 * @param valueMap
	 * @param data
	 * @param operationMap
	 * @param returnType
	 * @param isOrder
	 * @param isTransient
	 * @param rowmapping
	 */
	public SearchConfig(BeanConfig beanConfig, Map<String, Object> valueMap, Object data,
			Map<String, Operation> operationMap, Class<?> returnType, boolean isOrder, boolean isTransient,
			LRowMapping<T> rowmapping) {
		super(beanConfig, valueMap, data, operationMap, returnType);
		this.isOrder = isOrder;
		this.isTransient = isTransient;
		this.rowmapping = rowmapping;
	}

	/**
	 * @return the isOrder
	 */
	public boolean isOrder() {
		return isOrder;
	}

	/**
	 * @param isOrder the isOrder to set
	 */
	public void setOrder(boolean isOrder) {
		this.isOrder = isOrder;
	}

	/**
	 * @return the isTransient
	 */
	public boolean isTransient() {
		return isTransient;
	}

	/**
	 * @param isTransient the isTransient to set
	 */
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}

	/**
	 * @return the rowmapping
	 */
	public LRowMapping<T> getRowmapping() {
		return rowmapping;
	}

	/**
	 * @param rowmapping the rowmapping to set
	 */
	public void setRowmapping(LRowMapping<T> rowmapping) {
		this.rowmapping = rowmapping;
	}

}
