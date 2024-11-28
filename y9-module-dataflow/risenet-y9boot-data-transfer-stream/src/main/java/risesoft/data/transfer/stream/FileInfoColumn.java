package risesoft.data.transfer.stream;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

/**
 * 文件描述信息
 * 
 * 
 * @typeName FileInfoColumn
 * @date 2024年11月27日
 * @author lb
 */
public class FileInfoColumn extends Column {

	/**
	 * 文件名
	 */
	private String name;
	/**
	 * 文件完整路径
	 */
	private String path;
	/**
	 * 最后更新时间
	 */
	private long lastUpdateTime;

	/**
	 * 文件大小
	 */
	private long size;

	private Stream stream;

	public FileInfoColumn(Stream stream, long byteSize, String name, String path, long lastUpdateTime) {
		super(stream, Type.STREAM, byteSize, name);
		this.stream = stream;
		this.size = byteSize;
		this.path = path;
		this.lastUpdateTime = lastUpdateTime;
		this.name=name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	@Override
	public Long asLong() {
		return size;
	}

	@Override
	public Double asDouble() {
		return (double) size;
	}

	@Override
	public String asString() {
		return path + name;
	}

	@Override
	public Date asDate() {
		return new Date(lastUpdateTime);
	}

	@Override
	public byte[] asBytes() {
		return stream.getBytes();
	}

	@Override
	public Boolean asBoolean() {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "stream 列不支持转换为布尔类型");
	}

	@Override
	public BigDecimal asBigDecimal() {
		return new BigDecimal(size);
	}

	@Override
	public BigInteger asBigInteger() {
		return new BigInteger(size + "");
	}

}
