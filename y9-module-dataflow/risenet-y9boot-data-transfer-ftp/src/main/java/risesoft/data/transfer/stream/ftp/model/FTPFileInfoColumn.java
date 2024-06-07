package risesoft.data.transfer.stream.ftp.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.StringColumn;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

/**
 * ftp 文件信息用于文件传输这个是消费者和生产流定义下来的
 * 
 * @typeName FtpFileInfoColumn
 * @date 2024年3月5日
 * @author lb
 */
public class FTPFileInfoColumn extends Column {

	private String filePath;

	private FTPInfo ftpInfo;

	public FTPFileInfoColumn(String filePath, Type type, long byteSize, String name, FTPInfo ftpInfo) {
		super(filePath, type, byteSize, name);
		this.filePath = filePath;
		this.ftpInfo = ftpInfo;
	}

	@Override
	public Long asLong() {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "FTP文件信息无法转换为Long");
	}

	@Override
	public Double asDouble() {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "FTP文件信息无法转换为Double");
	}

	@Override
	public String asString() {
		return filePath;
	}

	@Override
	public Date asDate() {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "FTP文件信息无法转换为Date");
	}

	@Override
	public byte[] asBytes() {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "FTP文件信息无法转换为Byte");
	}

	@Override
	public Boolean asBoolean() {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "FTP文件信息无法转换为Boolean");
	}

	@Override
	public BigDecimal asBigDecimal() {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "FTP文件信息无法转换为BigDecimal");
	}

	@Override
	public BigInteger asBigInteger() {
		throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "FTP文件信息无法转换为BigInteger");
	}

	public String getFilePath() {
		return filePath;
	}



	public FTPInfo getFtpInfo() {
		return ftpInfo;
	}



	
}
