package risesoft.data.transfer.core.util.strings.handles.impl;

import risesoft.data.transfer.core.util.strings.handles.StringCastValueHandle;

/**
 * STRING TO SHORT
 * 
 * @typeName StringToShortHandle
 * @date 2024年1月29日
 * @author lb
 */
public class StringToShortHandle implements StringCastValueHandle<Short> {

	public Class<?>[] getTypes() {
		return new Class<?>[] { short.class, Short.class };
	}

	@Override
	public Short getValue(String value) {
		return Short.valueOf(value);
	}

}
