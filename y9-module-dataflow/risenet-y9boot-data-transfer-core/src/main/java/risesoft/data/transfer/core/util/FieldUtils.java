package risesoft.data.transfer.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldUtils {
	/**
	 * 拿到一个对象所有的字段 包括父类的 除了object
	 * 
	 * @param entiry
	 * @return
	 */
	public static List<Field> getFields(Class<?> entiry) {
		List<Field> fieldList = new ArrayList<>();
		Class<?> tempClass = entiry;
		while (tempClass != null && !tempClass.getName().equals("java.lang.Object")) {
			fieldList.addAll(Arrays.asList(tempClass.getDeclaredFields()));
			tempClass = tempClass.getSuperclass();
		}
		return fieldList;
	}

	public static String captureName(String name) {
		name = name.substring(0, 1).toLowerCase() + name.substring(1);
		return name;

	}

}
