package risesoft.data.transfer.core.util.strings.handles.impl;

import risesoft.data.transfer.core.util.strings.handles.StringCastValueHandle;

/**
 * STRING TO LONG
 * 
 * @typeName StringToLongHandle
 * @date 2024年1月29日
 * @author lb
 */
public class StringToLongHandle implements StringCastValueHandle<Long> {

	public Class<?>[] getTypes() {
		return new Class<?>[] { long.class, Long.class };
	}

	@Override
	public Long getValue(String value) {
		return Long.valueOf(value);
	}

}
