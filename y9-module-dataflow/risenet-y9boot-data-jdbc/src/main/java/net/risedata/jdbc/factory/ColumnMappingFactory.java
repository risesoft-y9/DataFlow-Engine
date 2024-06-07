package net.risedata.jdbc.factory;

import java.lang.reflect.Field;

import net.risedata.jdbc.annotations.mapping.ColMapping;
import net.risedata.jdbc.config.model.BeanConfig;
import net.risedata.jdbc.mapping.ColumnMapping;

/**
 * column mapping 构建工厂
 * 
 * @author lb
 *
 */
public class ColumnMappingFactory {
	public static ColumnMapping getInstance(BeanConfig bc,Field field) {
		ColMapping colMapping = field.getAnnotation(ColMapping.class);
		if (colMapping != null) {
           Class<? extends ColumnMapping> mappingClass = colMapping.value();
           ColumnMapping columnMapping =  InstanceFactoryManager.getInstance(mappingClass);
           return columnMapping.create(bc, field, colMapping.args());
		}
		return null;
	}
}
