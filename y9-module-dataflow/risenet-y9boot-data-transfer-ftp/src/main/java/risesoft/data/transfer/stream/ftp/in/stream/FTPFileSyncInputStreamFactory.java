package risesoft.data.transfer.stream.ftp.in.stream;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import cn.hutool.core.date.DateUtil;
import risesoft.data.transfer.core.channel.InChannel;
import risesoft.data.transfer.core.column.Column.Type;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.record.DefaultRecord;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.stream.FileInfoColumn;
import risesoft.data.transfer.stream.Stream;
import risesoft.data.transfer.stream.ftp.in.info.FtpConfig;
import risesoft.data.transfer.stream.ftp.model.FTPFileInfoColumn;
import risesoft.data.transfer.stream.ftp.utils.FTPUtils;
import risesoft.data.transfer.stream.ftp.utils.PattenUtil;

/**
 * 此类用于将ftp的文件信息读取成流信息
 * 
 * @typeName FTPFileSyncInputStreamFactory
 * @date 2024年3月4日
 * @author lb
 */
public class FTPFileSyncInputStreamFactory implements DataInputStreamFactory {

	class FileSyncInputDataInputStream implements DataInputStream {
		//这里需要构建一个ftp连接池 ps:一个inputData 可以处理多个数据连接
		private FTPClient ftpClient;

		FileSyncInputDataInputStream(FtpConfig ftpConfig) {
			this.ftpClient = FTPUtils.getClient(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUserName(),
					ftpConfig.getPassword(), ftpConfig.getEncoding(), ftpConfig.isActiveModel());
	
		}

		@Override
		public void close() throws Exception {
			try {
				ftpClient.logout();
				ftpClient.disconnect();
			} catch (IOException e) {
				logger.error(this, "关闭ftp客户端出错" + e.getMessage());
			}
		}

		@Override
		public void read(Data data, InChannel inChannel) {
			FtpConfig ftpConfig = (FtpConfig) data;
			try {
				logger.debug(this, "login");
				FTPFile ftpFile = getFileByFullPath(ftpClient, ftpConfig.getPath());
				try {
					readFiles(ftpFile, ftpClient, ftpConfig.getPath(), inChannel, maxDate, fileNameMatch);
				} catch (Exception e) {
					throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "从ftp获取文件列表时出错:" + e.getMessage());
				}
				inChannel.flush();
			} catch (Throwable e) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, e.getMessage(), e);
			}

		}

	}

	private Logger logger;
	private FtpConfig ftpConfig;
	private String fileNameMatch;
	private long maxDate = -1;

	public FTPFileSyncInputStreamFactory(FtpConfig ftpConfig, LoggerFactory loggerFactory) {
		this.ftpConfig = ftpConfig;
		logger = loggerFactory.getLogger(ftpConfig.getName());
	}

	@Override
	public void init() {

		if (!StringUtils.isEmpty(ftpConfig.getDate())) {
			try {
				maxDate = DateUtil.parse(ftpConfig.getDate(), "yyyy-MM-dd HH:mm:ss").getTime();
			} catch (Exception e) {
				throw new TransferException(CommonErrorCode.CONFIG_ERROR,
						"时间格式化错误请确保时间格式为yyyy-MM-dd HH:mm:ss " + e.getMessage());
			}

		}
		if (!StringUtils.isEmpty(ftpConfig.getFileName())) {
			this.fileNameMatch = ftpConfig.getFileName();
		}
	}

	@Override
	public DataInputStream getStream() {
		return new FileSyncInputDataInputStream(ftpConfig);
	}

	@Override
	public void close() throws Exception {

	}

	@Override
	public List<Data> splitToData(int executorSize) throws Exception {
		return Arrays.asList(this.ftpConfig);

	}

	/**
	 * 根据完整路径从ftp中获取文件
	 * 
	 * @param ftpClient
	 * @param path
	 * @return
	 * @throws IOException
	 */
	private FTPFile getFileByFullPath(FTPClient ftpClient, String path) throws IOException {
		int pathIndex = path.lastIndexOf('/');
		if (pathIndex != -1) {
			ftpClient.changeWorkingDirectory(path.substring(0, pathIndex));
		}

		String fileName = path.substring(pathIndex + 1);
		// 此时我们已经在文件的目录中，可以获取文件了
		FTPFile[] files = ftpClient.listFiles();
		for (FTPFile file : files) {
			if (file.getName().equals(fileName)) {
				return file;
			}
		}
		return null;
	}

	private void readFiles(FTPFile rootFile, FTPClient ftpClient, String rootPath, InChannel inChannel, long date,
			String fileNameMatch) throws IOException {
		if (rootFile == null) {
			FTPFile[] ftpFiles2 = ftpClient.listFiles();
			for (FTPFile ftpFile : ftpFiles2) {
				readTreeFiles(ftpFile, ftpClient, rootPath, inChannel, date, fileNameMatch);
			}
		} else {
			readTreeFiles(rootFile, ftpClient, rootPath, inChannel, date, fileNameMatch);
		}

	}

	private void readTreeFiles(FTPFile rootFile, FTPClient ftpClient, String rootPath, InChannel inChannel, long date,
			String fileNameMatch) throws IOException {
		if (rootFile.isDirectory()) {
			rootPath = (rootPath.endsWith("/") ? rootPath : rootPath + "/") + rootFile.getName();
			FTPFile[] ftpFiles2 = ftpClient.listFiles(rootPath);
			for (FTPFile ftpFile : ftpFiles2) {
				readTreeFiles(ftpFile, ftpClient, rootPath, inChannel, date, fileNameMatch);
			}
		} else {
			if (date != -1 && rootFile.getTimestamp().getTimeInMillis() < date) {
				return;
			}
			if (fileNameMatch != null && !PattenUtil.hasMatch(fileNameMatch, rootFile.getName())) {
				return;
			}
			DefaultRecord defaultRecord = new DefaultRecord();
			// 这里读取的信息为stream信息对象
			String fileName = new String(rootFile.getName().getBytes(Charset.forName(FTPUtils.DEFAULT_ENCODING)),
					"UTF-8");
			String filePath = ((rootPath.endsWith("/") ? rootPath : rootPath + "/") + "/" + fileName).replace("//",
					"/");
			defaultRecord.addColumn(new FileInfoColumn(new Stream() {

				@Override
				public void writer(OutputStream outputStream) {
					try {
						if (!ftpClient.retrieveFile(filePath, outputStream)) {
							throw TransferException.as(CommonErrorCode.RUNTIME_ERROR,
									filePath + "文件传输失败,未知原因，可能是FTP已经断开");
						}
					} catch (IOException e) {
						throw TransferException.as(CommonErrorCode.RUNTIME_ERROR,
								filePath + "文件传输失败,异常信息:" + e.getMessage());
					}

				}

				@Override
				public byte[] getBytes() {
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					writer(outputStream);
					return outputStream.toByteArray();
				}
			}, rootFile.getSize(), fileNameMatch, filePath, rootFile.getTimestamp().getTimeInMillis()));
			inChannel.writer(defaultRecord);
		}
	}

}
