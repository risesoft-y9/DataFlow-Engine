package risesoft.data.transfer.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClassUtils {
	public static <T> Class<T> getT(Object obj, int index) {
		Type sType = obj.getClass().getGenericSuperclass();
		Type[] generics = ((ParameterizedType) sType).getActualTypeArguments();
		@SuppressWarnings("unchecked")
		Class<T> mTClass = (Class<T>) generics[index];
		return mTClass;
	}

	public static List<Class<?>> getInterfaces(Class<?> interfaceClass) {
		List<Class<?>> arr = new ArrayList<Class<?>>();
		loadInterfaces(interfaceClass, arr);
		return arr;
	}

	private static void loadInterfaces(Class<?> interfaceClass, List<Class<?>> arr) {

		Class<?>[] interfaces = interfaceClass.getInterfaces();
		for (Class<?> class1 : interfaces) {
			arr.add(class1);
			loadInterfaces(class1, arr);
		}
	}

	public static <T> Class<T> getT(Object obj) {

		return getT(obj, 0);
	}

}
