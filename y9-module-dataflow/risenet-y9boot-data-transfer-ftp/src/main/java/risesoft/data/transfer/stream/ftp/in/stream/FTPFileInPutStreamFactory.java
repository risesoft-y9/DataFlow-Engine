package risesoft.data.transfer.stream.ftp.in.stream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.stream.in.DataInputStream;
import risesoft.data.transfer.core.stream.in.DataInputStreamFactory;
import risesoft.data.transfer.stream.ftp.utils.FTPUtils;

/**
 * ftp 文件流传输 这个类是符合标准流引擎传输协议的，
 * 但是对于生产者消费者模型来说这是不适用的，读取过快，和堆积必然会造成内存溢出,如果使用限流一定程度上可以解决溢出问题，但治标不治本 因此此类不推荐使用
 * 期待在后续的业务发展中使用到这个类， 如果您需要使用ftp文件传输请使用{@link FTPFileInfoStreamFactory} 
 *  ps:李博2024-03-04 通过大文件，多种方法测试后得出结论
 * 
 * @typeName FtpFileInPutStreamFactory
 * @date 2024年2月27日
 * @author lb
 */
public class FTPFileInPutStreamFactory implements DataInputStreamFactory {

	private Logger logger;
	private FtpConfig ftpConfig;

	public FTPFileInPutStreamFactory(FtpConfig ftpConfig, LoggerFactory loggerFactory) {
		this.ftpConfig = ftpConfig;
		ftpConfig.buffer = ftpConfig.buffer;
		logger = loggerFactory.getLogger(ftpConfig.name);
	}

	@Override
	public void init() {
	}

	@Override
	public DataInputStream getStream() {
		return new FTPFileInPutStream(FTPUtils.getClient(ftpConfig.host, ftpConfig.port, ftpConfig.userName,
				ftpConfig.password, ftpConfig.encoding), logger, ftpConfig.buffer);
	}

	@Override
	public void close() throws Exception {
	}

	@Override
	public List<Data> splitToData(int executorSize) throws Exception {
		FTPClient ftpClient = FTPUtils.getClient(ftpConfig.host, ftpConfig.port, ftpConfig.userName, ftpConfig.password,
				ftpConfig.encoding);
		try {
			logger.debug(this, "login");

			FTPFile ftpFile = getFileByFullPath(ftpClient, ftpConfig.path);

			List ftpEntiries = null;
			try {

				ftpEntiries = getFiles(ftpFile, ftpClient, ftpConfig.path);
			} catch (Exception e) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "从ftp获取文件列表时出错:" + e.getMessage());
			}
			if (logger.isDebug()) {
				logger.debug(this, "sub data " + ftpEntiries.size());
			}
			return ftpEntiries;
		} finally {
			ftpClient.logout();
			ftpClient.disconnect();
		}

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

	private List<FTPFileEntiry> getFiles(FTPFile rootFile, FTPClient ftpClient, String rootPath) throws IOException {
		List<FTPFileEntiry> ftpFiles = new ArrayList<FTPFileEntiry>();
		if (rootFile == null) {
			FTPFile[] ftpFiles2 = ftpClient.listFiles();
			for (FTPFile ftpFile : ftpFiles2) {
				getFiles(ftpFile, ftpFiles, ftpClient, rootPath);
			}
		} else {
			getFiles(rootFile, ftpFiles, ftpClient, rootPath);
		}

		return ftpFiles;
	}

	private void getFiles(FTPFile rootFile, List<FTPFileEntiry> ftpFiles, FTPClient ftpClient, String rootPath)
			throws IOException {
		if (rootFile.isDirectory()) {
			rootPath = rootPath + "/" + rootFile.getName();
			FTPFile[] ftpFiles2 = ftpClient.listFiles(rootPath);
			for (FTPFile ftpFile : ftpFiles2) {
				getFiles(ftpFile, ftpFiles, ftpClient, rootPath);
			}
		} else {
			ftpFiles.add(new FTPFileEntiry(rootPath + "/" + rootFile.getName(), rootFile));
		}
	}

	public static class FTPFileEntiry implements Data {
		private String absPath;
		private FTPFile ftpFile;

		public FTPFileEntiry(String absPath, FTPFile ftpFile) {
			super();
			this.absPath = absPath;
			this.ftpFile = ftpFile;
		}

		public String getAbsPath() {
			return absPath;
		}

		public FTPFile getFtpFile() {
			return ftpFile;
		}

	}
}
