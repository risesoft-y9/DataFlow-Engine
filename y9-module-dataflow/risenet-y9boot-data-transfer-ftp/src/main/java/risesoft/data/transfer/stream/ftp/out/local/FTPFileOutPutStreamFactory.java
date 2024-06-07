package risesoft.data.transfer.stream.ftp.out.local;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.net.ftp.FTPClient;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.record.Ack;
import risesoft.data.transfer.core.record.Record;
import risesoft.data.transfer.core.stream.out.DataOutputStream;
import risesoft.data.transfer.core.stream.out.DataOutputStreamFactory;
import risesoft.data.transfer.stream.ftp.model.FTPFileInfoColumn;
import risesoft.data.transfer.stream.ftp.model.FTPInfo;
import risesoft.data.transfer.stream.ftp.utils.FTPUtils;

/**
 * ftp 文件输出流 将FTP 的文件信息输出到指定目录的文件
 * 
 * @typeName FtpFileOutPutStreamFactory
 * @date 2024年2月26日
 * @author lb
 */
public class FTPFileOutPutStreamFactory implements DataOutputStreamFactory {

	private Logger logger;
	private LocalConfig localConfig;

	public FTPFileOutPutStreamFactory(LocalConfig localConfig, LoggerFactory loggerFactory) {
		logger = loggerFactory.getLogger(localConfig.getName());
		this.localConfig = localConfig;
		this.localConfig.setBufferSize(this.localConfig.getBufferSize() * 1024);
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
				try {
					for (int i = 0; i < record.getColumnNumber(); i++) {
						Column column = record.getColumn(i);
						if (column instanceof FTPFileInfoColumn) {
							FTPFileInfoColumn ftpFileInfoColumn = (FTPFileInfoColumn) column;
							FTPClient ftpClient = getFTPClient(ftpFileInfoColumn.getFtpInfo());
							String file = localConfig.getPath() + ftpFileInfoColumn.getFilePath();
							FileUtils.forceMkdir(new File(file.substring(0, file.lastIndexOf("/") + 1)));
							if (logger.isDebug()) {
								logger.debug(this, "transfer " + ftpFileInfoColumn.getFilePath() + " to " + file);
							}
							if (!ftpClient.retrieveFile(ftpFileInfoColumn.getFilePath(),
									new FileOutputStream(new File(file)))) {
								throw TransferException.as(CommonErrorCode.RUNTIME_ERROR,
										ftpFileInfoColumn.getFilePath() + "文件传输失败未知原因!目标文件" + file);
							}
						}
					}
					ack.confirm(record);
				} catch (Exception e) {
					ack.cancel(record, e, "从FTP 输出文件到本地出现异常:" + e.getMessage());
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
	public synchronized void close() throws Exception {
		Set<String> keySet = clientMap.keySet();
		for (String key : keySet) {
			FTPClient ftpClient = clientMap.get(key);
			if (logger.isDebug()) {
				logger.debug(this, "close ftp:" + key);
			}
			try {
				if (ftpClient.isConnected()) {
					ftpClient.logout();
					ftpClient.disconnect();
				}
			} catch (Exception e) {
				logger.error(this, "关闭连接:" + key + "时出错" + e.getMessage());
			}

		}
	}

	private Map<String, FTPClient> clientMap = new HashMap<String, FTPClient>();

	/**
	 * 获取FTPclient 经过测试 ftpclient 不允许一个连接同时下载多个文件
	 * 
	 * @param ftpInfo
	 * @return
	 */
	private FTPClient getFTPClient(FTPInfo ftpInfo) {
		String key = ftpInfo.getHost() + ftpInfo.getPort() + ftpInfo.getUserName() + Thread.currentThread().getId();
		FTPClient ftpClient = clientMap.get(key);
		if (ftpClient == null) {
			synchronized (clientMap) {
				ftpClient = clientMap.get(key);
				if (ftpClient == null) {
					if (logger.isDebug()) {
						logger.debug(this, "login ftp " + ftpInfo.getHost() + ":" + ftpInfo.getPort() + "/"
								+ ftpInfo.getUserName());
					}
					ftpClient = FTPUtils.getClient(ftpInfo.getHost(), ftpInfo.getPort(), ftpInfo.getUserName(),
							ftpInfo.getPassword(), FTPUtils.DEFAULT_ENCODING);
					ftpClient.setBufferSize(localConfig.getBufferSize());
					clientMap.put(key, ftpClient);

				}
			}
		}
		return ftpClient;
	}

}
