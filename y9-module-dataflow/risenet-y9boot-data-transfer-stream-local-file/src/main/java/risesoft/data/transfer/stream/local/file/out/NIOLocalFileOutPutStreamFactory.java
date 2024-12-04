package risesoft.data.transfer.stream.local.file.out;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.List;

import org.apache.commons.io.FileUtils;

import cn.hutool.core.io.FileUtil;
import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.record.Ack;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.core.stream.out.DataOutputStreamFactory;
import risesoft.data.transfer.stream.FileInfoColumn;
import risesoft.data.transfer.stream.StreamColumn;
import risesoft.data.transfer.stream.local.file.LocalFileInfoColumn;
import risesoft.data.transfer.stream.out.stream.LocalFileConfig;

/**
 * 本地文件流写入工厂, 仅支持解析FileInfo 类 将本地文件使用NIO的方式保存
 * 
 * @typeName NIOLocalFileOutPutStreamFactory
 * @date 2024年3月1日
 * @author lb
 */
public class NIOLocalFileOutPutStreamFactory implements DataOutputStreamFactory, DataOutputStream {
	private LocalFileConfig localFileConfig;

	private Logger logger;

	private String removePath;

	public NIOLocalFileOutPutStreamFactory(LocalFileConfig localFileConfig, LoggerFactory loggerFactory,
			@ConfigParameter(required = false, description = "需要移除的目录，用于将根目录替换掉", value = "\"\"") String removePath) {
		this.localFileConfig = localFileConfig;
		logger = loggerFactory.getLogger(localFileConfig.getName());
		this.removePath = removePath;
	}

	@Override
	public void init() {

	}

	@Override
	public DataOutputStream getStream() {
		return this;
	}

	@Override
	public void close() throws Exception {

	}

	@Override
	public void writer(List<Record> records, Ack ack) {
		for (Record record : records) {
			writer(record, ack);
		}
	}

	@Override
	public void writer(Record record, Ack ack) {
		try {
			List<Column> columns = record.getColumns();
			FileOutputStream fileOutputStream = null;
			for (Column column : columns) {
				if (!(column instanceof LocalFileInfoColumn)) {
					throw TransferException.as(CommonErrorCode.CONFIG_ERROR,
							"本地文件输出流只接受LocalFileInfoColumn此本地文件保存流只针对文件保存到本地，采取NIO的方式提升效率，用于本地文件拷贝");
				}
				LocalFileInfoColumn streamColumn = (LocalFileInfoColumn) column;
				String fileOrgPath = localFileConfig.getRootPath() + streamColumn.getPath().replace(removePath, "");
				FileUtils.forceMkdir(new File(fileOrgPath.substring(0, fileOrgPath.lastIndexOf("/") + 1)));
				try {
					fileOutputStream = new FileOutputStream(new File(fileOrgPath));
					streamColumn.getStream().writer(fileOutputStream);
				} finally {
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
				}
			}
			ack.confirm(record);
		} catch (Exception e) {
			logger.error(this, e.getMessage());
			ack.cancel(record, e, "输出文件报错!" + e.getMessage());
		}
	}

}
