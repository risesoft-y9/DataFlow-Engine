package risesoft.data.transfer.stream.ftp.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import risesoft.data.transfer.core.exception.CommonErrorCode;
import risesoft.data.transfer.core.exception.TransferException;

public class FTPUtils {

	public static final String DEFAULT_ENCODING = "ISO-8859-1";

	public static FTPClient getClient(String host, int port, String userName, String password, String encoding) {
		try {
			FTPClient ftpClient = new FTPClient();
			ftpClient.connect(host, port);
			ftpClient.setControlEncoding("ISO-8859-1");
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				ftpClient.disconnect();
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "网络原因连接ftp失败!");
			}
			if (!ftpClient.login(userName, password)) {
				throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "登录ftp失败!");
			}
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			
			return ftpClient;
		} catch (Exception e) {
			throw TransferException.as(CommonErrorCode.RUNTIME_ERROR, "连接ftp异常!" + e.getMessage());
		}

	}
}
