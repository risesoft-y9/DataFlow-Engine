package net.risedata.jdbc.mapping.impl;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.risedata.jdbc.mapping.CastHandleMapping;
import net.risedata.jdbc.mapping.HandleException;

public class ByteHandle extends SimpleHandleMapping<Byte> implements CastHandleMapping<Byte> {

	@Override
	public boolean isHandle(Class<?> cla) {
		return cla == Byte.class || cla == Byte.TYPE;
	}

	@Override
	Object getValue(Field field, Byte value, Class<?> type) {
		if (type == Byte.class) {
			return value;
		} else if (type == Byte.TYPE) {
			return value.byteValue();
		} else {
			throw new HandleException(type + "my no handle");
		}
	}

	@Override
	public Byte getValue(ResultSet set, String field) throws SQLException {
		return toValue(set, set.getByte(field));
	}

	@Override
	public Byte toValue(Object o) {
		if (o instanceof Byte) {
			return (Byte) o;
		}
		return Byte.parseByte(o.toString());
	}

}
