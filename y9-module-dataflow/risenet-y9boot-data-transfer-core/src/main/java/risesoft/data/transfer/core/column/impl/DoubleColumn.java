package risesoft.data.transfer.core.column.impl;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 小数
 * 
 * @typeName DoubleColumn
 * @date 2023年12月11日
 * @author lb
 */
public class DoubleColumn extends Column {

	public DoubleColumn(final String data, String name) {
		this(data, null == data ? 0 : data.length(), name);
		this.validate(data);
	}

	public DoubleColumn(Long data, String name) {
		this(data == null ? (String) null : String.valueOf(data), name);
	}

	public DoubleColumn(Integer data, String name) {
		this(data == null ? (String) null : String.valueOf(data), name);
	}

	/**
	 * Double无法表示准确的小数数据，我们不推荐使用该方法保存Double数据，建议使用String作为构造入参
	 * 
	 */
	public DoubleColumn(final Double data, String name) {
		this(data == null ? (String) null : new BigDecimal(String.valueOf(data)).toPlainString(), name);
	}

	/**
	 * Float无法表示准确的小数数据，我们不推荐使用该方法保存Float数据，建议使用String作为构造入参
	 * 
	 */
	public DoubleColumn(final Float data, String name) {
		this(data == null ? (String) null : new BigDecimal(String.valueOf(data)).toPlainString(), name);
	}

	public DoubleColumn(final BigDecimal data, String name) {
		this(null == data ? (String) null : data.toPlainString(), name);
	}

	public DoubleColumn(final BigInteger data, String name) {
		this(null == data ? (String) null : data.toString(), name);
	}

	public DoubleColumn(String name) {
		this((String) null, name);
	}

	private DoubleColumn(final String data, int byteSize, String name) {
		super(data, Column.Type.DOUBLE, byteSize, name);
	}

	@Override
	public BigDecimal asBigDecimal() {
		if (null == this.getRawData()) {
			return null;
		}

		try {
			return new BigDecimal((String) this.getRawData());
		} catch (NumberFormatException e) {
			throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("String[%s] 无法转换为Double类型 .", (String) this.getRawData()));
		}
	}

	@Override
	public Double asDouble() {
		if (null == this.getRawData()) {
			return null;
		}

		String string = (String) this.getRawData();

		boolean isDoubleSpecific = string.equals("NaN") || string.equals("-Infinity") || string.equals("+Infinity");
		if (isDoubleSpecific) {
			return Double.valueOf(string);
		}

		BigDecimal result = this.asBigDecimal();
		OverFlowUtil.validateDoubleNotOverFlow(result);

		return result.doubleValue();
	}

	@Override
	public Long asLong() {
		if (null == this.getRawData()) {
			return null;
		}

		BigDecimal result = this.asBigDecimal();
		OverFlowUtil.validateLongNotOverFlow(result.toBigInteger());

		return result.longValue();
	}

	@Override
	public BigInteger asBigInteger() {
		if (null == this.getRawData()) {
			return null;
		}

		return this.asBigDecimal().toBigInteger();
	}

	@Override
	public String asString() {
		if (null == this.getRawData()) {
			return null;
		}
		return (String) this.getRawData();
	}

	@Override
	public Boolean asBoolean() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Double类型无法转为Bool .");
	}

	@Override
	public Date asDate() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Double类型无法转为Date类型 .");
	}

	@Override
	public byte[] asBytes() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Double类型无法转为Bytes类型 .");
	}

	private void validate(final String data) {
		if (null == data) {
			return;
		}

		if (data.equalsIgnoreCase("NaN") || data.equalsIgnoreCase("-Infinity") || data.equalsIgnoreCase("Infinity")) {
			return;
		}

		try {
			new BigDecimal(data);
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("String[%s]无法转为Double类型 .", data));
		}
	}

}