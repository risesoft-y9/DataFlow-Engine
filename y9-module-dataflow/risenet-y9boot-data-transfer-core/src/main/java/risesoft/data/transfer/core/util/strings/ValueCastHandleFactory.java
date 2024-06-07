package risesoft.data.transfer.core.util.strings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import risesoft.data.transfer.core.util.ClassTools;
import risesoft.data.transfer.core.util.strings.handles.StringCastValueHandle;
import risesoft.data.transfer.core.util.strings.handles.impl.CastValueToStringHandle;

public class ValueCastHandleFactory {

	private static final Map<Class<?>, StringCastValueHandle<?>> HANDLES = new HashMap<Class<?>, StringCastValueHandle<?>>();

	private static final CastValueToStringHandle STRING_HANDLE = new CastValueToStringHandle();
	static {
		try {
			List<StringCastValueHandle> handles = ClassTools.getInstancesOfPack(
					"risesoft.data.transfer.core.util.strings.handles.impl", StringCastValueHandle.class);
			for (StringCastValueHandle handle : handles) {
				Class<?>[] types = handle.getTypes();
				for (Class<?> type : types) {
					HANDLES.put(type, handle);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static String getStirng(Object object) {
		return STRING_HANDLE.getValue(object);
	}

	public static <T> StringCastValueHandle<?> getHandle(Class<?> type) {
		return HANDLES.get(type);
	}

	public static <T> T castValue(String value, Class<T> cast) {
		StringCastValueHandle handle = getHandle(cast);
		if (handle == null) {
			throw new RuntimeException("未为" + cast + "找到对应的处理器");
		}
		return (T) handle.getValue(value);
	}

}
