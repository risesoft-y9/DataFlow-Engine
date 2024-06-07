package risesoft.data.transfer.core.util.strings.handles.impl;

import risesoft.data.transfer.core.util.strings.handles.StringCastValueHandle;

/**
 * string to int
 * 
 * @typeName StringToIntHandle
 * @date 2024年1月29日
 * @author lb
 */
public class StringToIntHandle implements StringCastValueHandle<Integer> {

	public Class<?>[] getTypes() {
		return new Class<?>[] { int.class, Integer.class };
	}

	@Override
	public Integer getValue(String value) {
		return (int) ((double) Double.valueOf(value));
	}

}
