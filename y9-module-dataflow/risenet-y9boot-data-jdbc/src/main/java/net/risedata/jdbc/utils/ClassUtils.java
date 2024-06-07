package net.risedata.jdbc.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.risedata.jdbc.repository.Repository;

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

	public static Map<String, GenerityModel[]> getGenerityClasss(Class<?> sourceClass) throws ClassNotFoundException {
		if (Repository.class.isAssignableFrom(sourceClass)) {
			Type[] types = sourceClass.getGenericInterfaces();
			Class<?>[] interfaces = sourceClass.getInterfaces();
			Map<String, GenerityModel[]> res = new HashMap<String, GenerityModel[]>();
			for (int i = 0; i < types.length; i++) {
				Type type = types[i];
				if (ParameterizedType.class.isAssignableFrom(type.getClass())) {
					Type[] genericTypes = ((ParameterizedType) type).getActualTypeArguments();
					GenerityModel[] generityModels = new GenerityModel[genericTypes.length];
					res.put(((ParameterizedType) type).getRawType().getTypeName(), generityModels);
					TypeVariable<?>[] typeNames = interfaces[i].getTypeParameters();
					for (int j = 0; j < genericTypes.length; j++) {
						Type genericType = genericTypes[j];
						generityModels[j] = new GenerityModel(typeNames[j].getName(),
								Thread.currentThread().getContextClassLoader().loadClass(genericType.getTypeName()));
					}
				}

			}
			return res;
		} else {
			Map<String, GenerityModel[]> res = null;
			for (Class<?> interfaceItem : sourceClass.getInterfaces()) {
				if (Repository.class.isAssignableFrom(interfaceItem)) {
					res = getGenerityClasss(interfaceItem);
					break;
				}
			}
			return res;
		}
	}

}
