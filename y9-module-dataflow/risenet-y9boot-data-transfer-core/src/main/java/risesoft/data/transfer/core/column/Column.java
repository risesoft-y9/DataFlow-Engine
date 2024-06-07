package risesoft.data.transfer.core.column;

import com.alibaba.fastjson.JSON;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 一列数据
 * 
 * @typeName Column
 * @date 2023年12月11日
 * @author lb
 */
public abstract class Column {

	private Type type;

	private Object rawData;

	private long byteSize;

	private String name;

	public Column(final Object object, final Type type, long byteSize, String name) {
		this.rawData = object;
		this.type = type;
		this.byteSize = byteSize;
		this.name = name;
		if (name == null) {
			throw new RuntimeException("没有名字");
		}
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getRawData() {
		return this.rawData;
	}

	public Type getType() {
		return this.type;
	}

	public long getByteSize() {
		return this.byteSize;
	}

	protected void setType(Type type) {
		this.type = type;
	}

	protected void setRawData(Object rawData) {
		this.rawData = rawData;
	}

	protected void setByteSize(int byteSize) {
		this.byteSize = byteSize;
	}

	public abstract Long asLong();

	public abstract Double asDouble();

	public abstract String asString();

	public abstract Date asDate();

	public abstract byte[] asBytes();

	public abstract Boolean asBoolean();

	public abstract BigDecimal asBigDecimal();

	public abstract BigInteger asBigInteger();

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}

	public enum Type {
		BAD, NULL, INT, LONG, DOUBLE, STRING, BOOL, DATE, BYTES, STREAM
	}

	public String getName() {
		return this.name;
	}
}
