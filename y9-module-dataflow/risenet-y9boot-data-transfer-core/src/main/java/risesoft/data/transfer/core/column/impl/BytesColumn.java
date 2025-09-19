package risesoft.data.transfer.core.column.impl;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

import org.apache.commons.lang3.ArrayUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * byte数组列数据
 * 
 * @typeName BytesColumn
 * @date 2023年12月11日
 * @author lb
 */
public class BytesColumn extends Column {
	public BytesColumn() {
		this(null);
	}

	public BytesColumn(String name) {
		this(null, name);
	}

	public BytesColumn(byte[] bytes, String name) {
		super(ArrayUtils.clone(bytes), Column.Type.BYTES, null == bytes ? 0 : bytes.length, name);
	}

	@Override
	public byte[] asBytes() {
		if (null == this.getRawData()) {
			return null;
		}

		return (byte[]) this.getRawData();
	}

	@Override
	public String asString() {
		if (null == this.getRawData()) {
			return null;
		}

		try {
			return ColumnCast.bytes2String(this);
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("Bytes[%s]不能转为String .", this.toString()));
		}
	}

	@Override
	public Long asLong() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Bytes类型不能转为Long .");
	}

	@Override
	public BigDecimal asBigDecimal() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Bytes类型不能转为BigDecimal .");
	}

	@Override
	public BigInteger asBigInteger() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Bytes类型不能转为BigInteger .");
	}

	@Override
	public Double asDouble() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Bytes类型不能转为Long .");
	}

	@Override
	public Date asDate() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Bytes类型不能转为Date .");
	}

	@Override
	public Boolean asBoolean() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Bytes类型不能转为Boolean .");
	}
}
