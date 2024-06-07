package risesoft.data.transfer.core.column.impl;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 布尔类型列数据
 * 
 * @typeName BoolColumn
 * @date 2023年12月11日
 * @author lb
 */
public class BoolColumn extends Column {

	public BoolColumn(Boolean bool, String name) {
		super(bool, Column.Type.BOOL, 1, name);
	}

	public BoolColumn(final String data, String name) {
		this(true, name);
		this.validate(data);
		if (null == data) {
			this.setRawData(null);
			this.setByteSize(0);
		} else {
			this.setRawData(Boolean.valueOf(data));
			this.setByteSize(1);
		}
		return;
	}

	public BoolColumn(String name) {
		super(null, Column.Type.BOOL, 1, name);
	}

	@Override
	public Boolean asBoolean() {
		if (null == super.getRawData()) {
			return null;
		}

		return (Boolean) super.getRawData();
	}

	@Override
	public Long asLong() {
		if (null == this.getRawData()) {
			return null;
		}

		return this.asBoolean() ? 1L : 0L;
	}

	@Override
	public Double asDouble() {
		if (null == this.getRawData()) {
			return null;
		}

		return this.asBoolean() ? 1.0d : 0.0d;
	}

	@Override
	public String asString() {
		if (null == super.getRawData()) {
			return null;
		}

		return this.asBoolean() ? "true" : "false";
	}

	@Override
	public BigInteger asBigInteger() {
		if (null == this.getRawData()) {
			return null;
		}

		return BigInteger.valueOf(this.asLong());
	}

	@Override
	public BigDecimal asBigDecimal() {
		if (null == this.getRawData()) {
			return null;
		}

		return BigDecimal.valueOf(this.asLong());
	}

	@Override
	public Date asDate() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Bool类型不能转为Date .");
	}

	@Override
	public byte[] asBytes() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Boolean类型不能转为Bytes .");
	}

	private void validate(final String data) {
		if (null == data) {
			return;
		}

		if ("true".equalsIgnoreCase(data) || "false".equalsIgnoreCase(data)) {
			return;
		}

		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, String.format("String[%s]不能转为Bool .", data));
	}
}
