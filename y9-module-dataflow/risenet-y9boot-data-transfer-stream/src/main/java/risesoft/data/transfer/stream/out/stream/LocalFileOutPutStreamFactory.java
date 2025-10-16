package risesoft.data.transfer.stream.out.stream;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import org.apache.commons.io.FileUtils;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.record.Ack;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.core.stream.out.DataOutputStreamFactory;
import risesoft.data.transfer.stream.StreamColumn;

/**
 * 本地文件流写入工厂, 仅支持解析 streamColumn 类 支持多线程分片保存文件
 * 不建议使用此类，多线程分片不适用于本地文件存储，会导致源头处流浪堆积从而发生内存溢出的情况
 * 
 * @typeName LocalFileOutPutStreamFactory
 * @date 2024年3月1日
 * @author lb
 */
public class LocalFileOutPutStreamFactory implements DataOutputStreamFactory {

	private LocalFileConfig localFileConfig;

	private Logger logger;

	public LocalFileOutPutStreamFactory(LocalFileConfig localFileConfig, LoggerFactory loggerFactory) {
		this.localFileConfig = localFileConfig;
		logger = loggerFactory.getLogger(localFileConfig.name);
	}

	@Override
	public void init() {

	}

	@Override
	public DataOutputStream getStream() {
		return new DataOutputStream() {

			@Override
			public void close() throws Exception {
			}

			@Override
			public void writer(Record record, Ack ack) {
				RandomAccessFile raf = null;
				FileChannel fileChannel = null;
				try {
					List<Column> columns = record.getColumns();
					for (Column column : columns) {
						if (!(column instanceof StreamColumn)) {
							throw TransferException.as(CommonErrorCode.CONFIG_ERROR, "本地文件输出流只接受StreamColumn");
						}
						StreamColumn streamColumn = (StreamColumn) column;
						String fileOrgPath = localFileConfig.rootPath + streamColumn.getName();
						FileUtils.forceMkdir(new File(fileOrgPath.substring(0, fileOrgPath.lastIndexOf("/") + 1)));
						raf = new RandomAccessFile(fileOrgPath, "rw");
						fileChannel = raf.getChannel().position(streamColumn.getStart());
						ByteBuffer buffer = ByteBuffer.wrap(streamColumn.asBytes(), 0, (int) streamColumn.getByteSize());
						while (buffer.hasRemaining()) {
							fileChannel.write(buffer);
						}
					}
					ack.confirm(record);
				} catch (Exception e) {
					logger.error(this, e.getMessage());
					ack.cancel(record, e, "输出文件报错!" + e.getMessage());
				} finally {
					if(raf != null) {
						try {
							raf.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if(fileChannel != null) {
						try {
							fileChannel.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}

			@Override
			public void writer(List<Record> records, Ack ack) {
				for (Record record : records) {
					writer(record, ack);
				}
			}
		};
	}

	@Override
	public void close() throws Exception {
	}

}
