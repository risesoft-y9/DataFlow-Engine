package risesoft.data.transfer.core.util.strings.handles.impl;


import risesoft.data.transfer.core.util.strings.handles.StringCastValueHandle;

/**
 * STRING TO byte
 * 
 * @typeName StringToByteHandle
 * @date 2024年1月29日
 * @author lb
 */
public class StringToByteHandle implements StringCastValueHandle<Byte> {

	public Class<?>[] getTypes() {
		return new Class<?>[] { byte.class, Byte.class };
	}

	@Override
	public Byte getValue(String value) {
		return Byte.valueOf(value);
	}

}
