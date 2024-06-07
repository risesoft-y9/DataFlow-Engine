package risesoft.data.transfer.core.util.strings.handles.impl;

import risesoft.data.transfer.core.util.strings.handles.StringCastValueHandle;

/**
 * STRING TO STRING
 * 
 * @typeName StringToStringHandle
 * @date 2024年1月29日
 * @author lb
 */
public class StringToStringHandle implements StringCastValueHandle<String> {

	public Class<?>[] getTypes() {
		return new Class<?>[] { String.class };
	}

	@Override
	public String getValue(String value) {
		return value;
	}

}
