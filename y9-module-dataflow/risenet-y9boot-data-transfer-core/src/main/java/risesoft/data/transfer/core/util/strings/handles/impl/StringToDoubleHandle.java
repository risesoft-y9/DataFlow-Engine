package risesoft.data.transfer.core.util.strings.handles.impl;

import risesoft.data.transfer.core.util.strings.handles.StringCastValueHandle;

/**
 * string to double
 * 
 * @typeName StringToDoubleHandle
 * @date 2024年1月29日
 * @author lb
 */
public class StringToDoubleHandle implements StringCastValueHandle<Double> {

	public Class<?>[] getTypes() {
		return new Class<?>[] { double.class, Double.class };
	}

	@Override
	public Double getValue(String value) {
		return Double.valueOf(value);
	}

}
