package risesoft.data.transfer.core.column.impl;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 字符串列数据
 * 
 * @typeName StringColumn
 * @date 2023年12月11日
 * @author lb
 */
public class StringColumn extends Column {

	public StringColumn() {
		this((String) null, null);
	}

	
	public StringColumn(final String rawData, String name) {
		super(rawData, Column.Type.STRING, (null == rawData ? 0 : rawData.length()), name);
	
	}


	@Override
	public String asString() {
		if (null == this.getRawData()) {
			return null;
		}

		return (String) this.getRawData();
	}

	private void validateDoubleSpecific(final String data) {
		if ("NaN".equals(data) || "Infinity".equals(data) || "-Infinity".equals(data)) {
			throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("String[\"%s\"]属于Double特殊类型，不能转为其他类型 .", data));
		}

		return;
	}

	@Override
	public BigInteger asBigInteger() {
		if (null == this.getRawData()) {
			return null;
		}

		this.validateDoubleSpecific((String) this.getRawData());

		try {
			return this.asBigDecimal().toBigInteger();
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("String[\"%s\"]不能转为BigInteger .", this.asString()));
		}
	}

	@Override
	public Long asLong() {
		if (null == this.getRawData()) {
			return null;
		}

		this.validateDoubleSpecific((String) this.getRawData());

		try {
			BigInteger integer = this.asBigInteger();
			OverFlowUtil.validateLongNotOverFlow(integer);
			return integer.longValue();
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("String[\"%s\"]不能转为Long .", this.asString()));
		}
	}

	@Override
	public BigDecimal asBigDecimal() {
		if (null == this.getRawData()) {
			return null;
		}

		this.validateDoubleSpecific((String) this.getRawData());

		try {
			return new BigDecimal(this.asString());
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("String [\"%s\"] 不能转为BigDecimal .", this.toString()));
		}
	}

	@Override
	public Double asDouble() {
		if (null == this.getRawData()) {
			return null;
		}

		String data = (String) this.getRawData();
		if ("NaN".equals(data)) {
			return Double.NaN;
		}

		if ("Infinity".equals(data)) {
			return Double.POSITIVE_INFINITY;
		}

		if ("-Infinity".equals(data)) {
			return Double.NEGATIVE_INFINITY;
		}

		BigDecimal decimal = this.asBigDecimal();
		OverFlowUtil.validateDoubleNotOverFlow(decimal);

		return decimal.doubleValue();
	}

	@Override
	public Boolean asBoolean() {
		if (null == this.getRawData()) {
			return null;
		}

		if ("true".equalsIgnoreCase(this.asString())) {
			return true;
		}

		if ("false".equalsIgnoreCase(this.asString())) {
			return false;
		}

		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
				String.format("String[\"%s\"]不能转为Bool .", this.asString()));
	}

	@Override
	public Date asDate() {
		try {
			return ColumnCast.string2Date(this);
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("String[\"%s\"]不能转为Date .", this.asString()));
		}
	}

	@Override
	public byte[] asBytes() {
		try {
			return ColumnCast.string2Bytes(this);
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("String[\"%s\"]不能转为Bytes .", this.asString()));
		}
	}
}
