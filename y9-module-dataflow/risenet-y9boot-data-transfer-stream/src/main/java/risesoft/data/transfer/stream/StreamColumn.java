package risesoft.data.transfer.stream;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

/**
 * 流列对象
 * 
 * @typeName StreamColumn
 * @date 2024年2月28日
 * @author lb
 */
public class StreamColumn extends Column {

	private int start;
	private int end;
	private long streamSize;

	/**
	 * 创建流列
	 * 
	 * @param data     数据流
	 * @param byteSize byte 大小
	 * @param name     流的名字
	 * @param start    这条数据在流中的起始位置
	 * @param end      这条数据在流中的结束任务
	 */
	public StreamColumn(byte[] data, int byteSize, String name, int start, int end, long streamSize) {

		super(data, Type.STREAM, byteSize, name);
		this.start = start;
		this.end = end;
		this.streamSize = streamSize;
	}

	
	
	public int getStart() {
		return start;
	}



	public void setStart(int start) {
		this.start = start;
	}



	public int getEnd() {
		return end;
	}

	public long getStreamSize() {
		return streamSize;
	}

	@Override
	public Long asLong() {
		return (long) (end - start);
	}

	@Override
	public Double asDouble() {
		return (double) (end - start);
	}

	@Override
	public String asString() {
		return new String(this.asBytes());
	}

	@Override
	public Date asDate() {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "stream 列不支持转换为时间类型");
	}

	@Override
	public byte[] asBytes() {
		return (byte[]) this.getRawData();
	}

	@Override
	public Boolean asBoolean() {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "stream 列不支持转换为布尔值");
	}

	@Override
	public BigDecimal asBigDecimal() {
		return new BigDecimal(end - start);
	}

	@Override
	public BigInteger asBigInteger() {
		return new BigInteger((end - start) + "");
	}

}
