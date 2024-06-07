package risesoft.data.transfer.core.util.strings.handles.impl;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import risesoft.data.transfer.core.util.strings.handles.StringCastValueHandle;

/**
 * string to date
 * 
 * @typeName StringToDateHandle
 * @date 2024年1月29日
 * @author lb
 */
public class StringToDateHandle implements StringCastValueHandle<Date> {

	
	public Class<?>[] getTypes() {
		return new Class<?>[] { Date.class};
	}
	@Override
	public Date getValue(String value) {
		try {
			return DateUtils.parseDate(value);
		} catch (ParseException e) {
			throw new RuntimeException("转时间失败!");
		}
	}

}
