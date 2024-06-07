package risesoft.data.transfer.core.util.strings.handles.impl;

import java.math.BigDecimal;

import risesoft.data.transfer.core.util.strings.handles.StringCastValueHandle;

/**
 * string to big
 * @typeName StringToBigHandle
 * @date 2024年1月29日
 * @author lb
 */
public class StringToBigHandle implements StringCastValueHandle<BigDecimal> {

	
	public Class<?>[] getTypes() {
		return new Class<?>[]{BigDecimal.class};
	}

	@Override
	public BigDecimal getValue(String value) {
		return new BigDecimal(value);
	}

}
