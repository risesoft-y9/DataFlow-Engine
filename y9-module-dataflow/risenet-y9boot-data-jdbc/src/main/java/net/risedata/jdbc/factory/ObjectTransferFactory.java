package net.risedata.jdbc.factory;

import java.util.ArrayList;
import java.util.List;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.exception.ConfigException;
import net.risedata.jdbc.exception.InstanceException;

/**
 * 对象的转换对象必须都存在与 {@link BeanConfigFactory} 中
 * 
 * @author libo 2021年6月24日
 */
public class ObjectTransferFactory {
	/**
	 * 
	 * @param <T>        返回符合returnType 的对象
	 * @param transfer   转移的对象
	 * @param returnType 目标class
	 * @return
	 */
	public static <T> T transferInstance(Object transfer, Class<T> returnType) {
		BeanConfig transferConfig = BeanConfigFactory.getInstance(transfer);
		BeanConfig returnConfig = BeanConfigFactory.getInstance(returnType);
		if (transferConfig == null || returnConfig == null) {
			throw new ConfigException(transfer.getClass() + " or " + returnType + " is no bean config ");
		}
		List<FieldConfig> returnFieldConfigs = returnConfig.getAllFields();
		Object returnObject = null;
		try {
			returnObject = returnConfig.getConstructor().newInstance();
			FieldConfig f = null;
			for (FieldConfig field : returnFieldConfigs) {
				f = transferConfig.getField(field.getFieldName());
				if (f != null && f.getFieldType() == field.getFieldType()) {
					field.getField().set(returnObject, f.getField().get(transfer));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new InstanceException("An error occurred while creating the object " + e.getMessage());
		}
		return returnType.cast(returnObject);
	}

	public static <T> List<T> transferInstances(List<?> transfers, Class<T> returnType) {

		List<T> ret = new ArrayList<>();
		for (Object object : transfers) {
			ret.add(transferInstance(object, returnType));
		}
		return ret;
	}
}
