package net.risedata.jdbc;


import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.config.model.FieldConfig;
import net.risedata.jdbc.executor.jdbc.JDBC;
import net.risedata.jdbc.factory.HandleMappingFactory;

/**
 * 处理bean 之间的 映射
 * @author libo
 *2020年12月10日
 */
public class Mapping extends JDBC{
	/**
	 * 将valuemap 里面值转换为 bean 对应的类型值
	 * @param configId 配置的id 可以是string  可以是class 可以是 对象
	 * @param valueMap 装换前的map
	 * @return 
	 */
    public  static void valueMapParseBeanType(Object configId,Map<String, Object> valueMap){
    	BeanConfig bc = getConfig(configId);
    	FieldConfig fc = null;
    	Set<String> keys = valueMap.keySet();
    	Object value ;
    	for (String key : keys) {
			fc = bc.getField(key);
			if (fc!=null) {
				value = valueMap.get(key);
				if (value!=null&&StringUtils.isNotBlank(value.toString())) {					
					valueMap.put(key, HandleMappingFactory.parse(value, fc.getFieldType()));
				}
			}
		}
    }
}
