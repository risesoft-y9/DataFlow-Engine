package risesoft.data.transfer.core.util.strings.handles.impl;

import java.util.Date;

import com.alibaba.fastjson2.util.DateUtils;

import risesoft.data.transfer.core.util.strings.handles.CastValueHandle;

/**
 * 将object 对象转换为string
 * 
 * @author libo 2021年7月22日
 */
public class CastValueToStringHandle implements CastValueHandle<String, Object> {

	public Class<?>[] getTypes() {
		return new Class<?>[0];
	}

	@Override
	public String getValue(Object value) {
		String val = null;
		if (value instanceof Date) {
			val = DateUtils.format((Date) value);
		} else {
			val = String.valueOf(value);
		}
		return val;
	}

}
