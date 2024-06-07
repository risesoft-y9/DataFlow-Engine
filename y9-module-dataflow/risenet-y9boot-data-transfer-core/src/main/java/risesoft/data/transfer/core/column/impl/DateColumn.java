package risesoft.data.transfer.core.column.impl;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * 时间类型
 * 
 * @typeName DateColumn
 * @date 2023年12月11日
 * @author lb
 */
public class DateColumn extends Column {

	private DateType subType = DateType.DATETIME;

	public static enum DateType {
		DATE, TIME, DATETIME
	}

	/**
	 * 构建值为null的DateColumn，使用Date子类型为DATETIME
	 */
	public DateColumn(String name) {
		this((Long) null, name);
	}

	/**
	 * 构建值为stamp(Unix时间戳)的DateColumn，使用Date子类型为DATETIME 实际存储有date改为long的ms，节省存储
	 */
	public DateColumn(final Long stamp, String name) {
		super(stamp, Column.Type.DATE, (null == stamp ? 0 : 8), name);
	}

	/**
	 * 构建值为date(java.util.Date)的DateColumn，使用Date子类型为DATETIME
	 */
	public DateColumn(final Date date, String name) {
		this(date == null ? null : date.getTime(), name);
	}

	/**
	 * 构建值为date(java.sql.Date)的DateColumn，使用Date子类型为DATE，只有日期，没有时间
	 */
	public DateColumn(final java.sql.Date date, String name) {
		this(date == null ? null : date.getTime(), name);
		this.setSubType(DateType.DATE);
	}

	/**
	 * 构建值为time(java.sql.Time)的DateColumn，使用Date子类型为TIME，只有时间，没有日期
	 */
	public DateColumn(final java.sql.Time time, String name) {
		this(time == null ? null : time.getTime(), name);
		this.setSubType(DateType.TIME);
	}

	/**
	 * 构建值为ts(java.sql.Timestamp)的DateColumn，使用Date子类型为DATETIME
	 */
	public DateColumn(final java.sql.Timestamp ts, String name) {
		this(ts == null ? null : ts.getTime(), name);
		this.setSubType(DateType.DATETIME);
	}

	@Override
	public Long asLong() {

		return (Long) this.getRawData();
	}

	@Override
	public String asString() {
		try {
			return ColumnCast.date2String(this);
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT,
					String.format("Date[%s]类型不能转为String .", this.toString()));
		}
	}

	@Override
	public Date asDate() {
		if (null == this.getRawData()) {
			return null;
		}

		return new Date((Long) this.getRawData());
	}

	@Override
	public byte[] asBytes() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Date类型不能转为Bytes .");
	}

	@Override
	public Boolean asBoolean() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Date类型不能转为Boolean .");
	}

	@Override
	public Double asDouble() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Date类型不能转为Double .");
	}

	@Override
	public BigInteger asBigInteger() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Date类型不能转为BigInteger .");
	}

	@Override
	public BigDecimal asBigDecimal() {
		throw TransferException.as(CommonErrorCode.CONVERT_NOT_SUPPORT, "Date类型不能转为BigDecimal .");
	}

	public DateType getSubType() {
		return subType;
	}

	public void setSubType(DateType subType) {
		this.subType = subType;
	}
}