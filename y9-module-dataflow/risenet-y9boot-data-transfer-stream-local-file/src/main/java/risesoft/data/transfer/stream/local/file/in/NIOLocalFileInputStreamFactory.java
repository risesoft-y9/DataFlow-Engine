package risesoft.data.transfer.stream.local.file.in;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import risesoft.data.transfer.core.channel.InChannel;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.data.StringData;
import risesoft.data.transfer.core.exception.FrameworkErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigField;
import risesoft.data.transfer.core.factory.annotations.ConfigParameter;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.record.DefaultRecord;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.stream.Stream;
import risesoft.data.transfer.stream.local.file.LocalFileInfoColumn;

/**
 * 以nio的形式获取文件信息的工厂
 * 
 * 
 * @typeName NIOLocalFileInputStreamFactory
 * @date 2024年11月28日
 * @author lb
 */
public class NIOLocalFileInputStreamFactory implements DataInputStreamFactory, DataInputStream {

	private String[] paths;

	private Logger logger;

	/**
	 * 
	 * @param rootPath 文件目录 多个采取,号分割
	 * @param name     日志名
	 */
	public NIOLocalFileInputStreamFactory(@ConfigParameter(required = true, description = "目录") String rootPath,
			@ConfigParameter(description = "名字", value = "NIOLocal") String name, LoggerFactory loggerFactory) {
		paths = rootPath.split(",");
		logger = loggerFactory.getLogger(name);
	}

	@Override
	public void init() {

	}

	@Override
	public DataInputStream getStream() {
		return this;
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public List<Data> splitToData(int executorSize) throws Exception {
		List<Data> datas = new ArrayList<Data>();
		for (String data : paths) {
			datas.add(new StringData(data));
		}
		return datas;
	}

	@Override
	public void read(Data data, InChannel inChannel) {
		File f = new File(((StringData) data).getValue());

	}

	private void read(File file, InChannel inChannel) {
		if (file.isDirectory()) {

		} else {
			Record record = new DefaultRecord();
			record.addColumn(new LocalFileInfoColumn(new Stream() {

				@Override
				public void writer(OutputStream outputStream) {
					FileInputStream sourceStream = null;
					FileChannel sourceChannel = null;
					try {
						sourceStream = new FileInputStream(file);
						if (outputStream instanceof FileOutputStream) {
							// 获取文件通道
							sourceChannel = sourceStream.getChannel();
							// 获取源文件的大小
							long fileSize = sourceChannel.size();
							// 分配目标文件的存储空间
							((FileOutputStream) outputStream).getChannel().transferFrom(sourceChannel, 0, fileSize);
							return;
						}
						sourceStream.transferTo(outputStream);
					} catch (IOException e) {
						throw TransferException.as(FrameworkErrorCode.RUNTIME_ERROR, "输出文件时发生异常，异常信息:" + e.getMessage(),
								e);
					} finally {
						try {
							if (sourceChannel != null)
								sourceChannel.close();
							if (sourceStream != null)
								sourceStream.close();

						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

				@Override
				public byte[] getBytes() {
					FileInputStream sourceStream = null;
					try {
						sourceStream = new FileInputStream(file);
						return sourceStream.readAllBytes();
					} catch (Exception e) {
						throw new RuntimeException(e);
					} finally {
						if (sourceStream != null) {
							try {
								sourceStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

				}
			}, file.length(), file.getName(), file.getAbsolutePath(), file.lastModified()));
			inChannel.writer(record);
		}
	}

}
