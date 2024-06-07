package net.risedata.jdbc.mapping;

import java.lang.reflect.Field;

import net.risedata.jdbc.config.model.BeanConfig;

/**
   *   对于行的映射解决一对多问题
 * 
 * @author lb
 *
 */
public interface ColumnMapping {

	ColumnMapping create(BeanConfig bc,Field field,String[] args);
	 
	void handle(Object value,BeanConfig bc);
	
}
