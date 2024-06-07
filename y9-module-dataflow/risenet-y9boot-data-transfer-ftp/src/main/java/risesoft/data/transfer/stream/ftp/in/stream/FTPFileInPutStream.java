package risesoft.data.transfer.stream.ftp.in.stream;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;

import risesoft.data.transfer.core.channel.InChannel;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.record.DefaultRecord;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.stream.StreamColumn;
import risesoft.data.transfer.stream.ftp.in.stream.FTPFileInPutStreamFactory.FTPFileEntiry;

/**
 * FTP 文件输入流
 * 
 * @typeName FTPFileInPutStream
 * @date 2024年2月27日
 * @author lb
 */
public class FTPFileInPutStream implements DataInputStream {

	private FTPClient ftpClient;

	private Logger logger;

	private int bufferSize;

	public FTPFileInPutStream(FTPClient ftpClient, Logger logger, int bufferSize) {
		super();
		this.ftpClient = ftpClient;
		this.logger = logger;
		this.bufferSize = bufferSize;
	}

	@Override
	public void close() throws Exception {
		ftpClient.logout();
		ftpClient.disconnect();
	}

	@SuppressWarnings("unused")
	@Override
	public void read(Data data, InChannel inChannel) {
		FTPFileEntiry ftpEntiry = (FTPFileEntiry) data;
		DefaultRecord defaultRecord = null;
		try {
			InputStream inputStream = ftpClient.retrieveFileStream(ftpEntiry.getAbsPath());
			byte[] datas = new byte[bufferSize];
			int size = -1;
			int start = 0;
			int end = 0;
			int readSize = 0;
			int c = 0;
			while ((size = inputStream.read(datas)) != -1) {
				defaultRecord = new DefaultRecord();
				end += size;
				readSize += size;
				defaultRecord.addColumn(new StreamColumn( datas, size, ftpEntiry.getAbsPath(), start, end,
						ftpEntiry.getFtpFile().getSize()));
				inChannel.writer(defaultRecord);
				datas = new byte[bufferSize];
				start = end;
			}
			defaultRecord = null;
			inputStream.close();
			ftpClient.disconnect();
			ftpClient.logout();
		} catch (IOException e) {
			// 此时属于脏数据
			if (defaultRecord != null) {
				inChannel.collectDirtyRecord(defaultRecord, e, e.getMessage());
			}
		} finally {
			inChannel.flush();
		}
	}

}
