package risesoft.data.transfer.stream.ftp;

import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;

import risesoft.data.transfer.core.Engine;
import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.factory.BeanFactory;
import risesoft.data.transfer.core.listener.impl.ResultJobListener;
import risesoft.data.transfer.core.log.Logger;
import risesoft.data.transfer.core.log.LoggerFactory;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.stream.ftp.in.stream.FTPFileInPutStreamFactory;
import risesoft.data.transfer.stream.ftp.in.stream.FtpConfig;
import risesoft.data.transfer.stream.ftp.utils.FTPUtils;

/**
 * 用来测试的
 * 
 * @typeName Test
 * @date 2024年3月4日
 * @author lb
 */
public class Test {

	public static void main(String[] args) throws Exception {

		
		FtpConfig ftpConfig = BeanFactory.getInstance(FtpConfig.class,
				Configuration.from(" {\r\n" + "				\"host\":\"10.192.198.196\",\r\n"
						+ "                \"port\":82,\r\n" + "                \"userName\":\"admin\",\r\n"
						+ "                \"password\":\"ftp132456\",\r\n" + "                \"path\":\"/\"\r\n"
						+ "			}"));

		System.out.println(ftpConfig.getUserName());
		System.out.println(ftpConfig.getPassword());
		FTPFileInPutStreamFactory ftpFileInPutStreamFactory = new FTPFileInPutStreamFactory(ftpConfig,
				new LoggerFactory() {

					@Override
					public Logger getLogger(Class<?> type) {
						// TODO Auto-generated method stub
						return new Logger() {

							@Override
							public boolean isInfo() {
								// TODO Auto-generated method stub
								return false;
							}

							@Override
							public boolean isError() {
								// TODO Auto-generated method stub
								return false;
							}

							@Override
							public boolean isDebug() {
								// TODO Auto-generated method stub
								return false;
							}

							@Override
							public void info(Object source, String msg) {
								// TODO Auto-generated method stub

							}

							@Override
							public void error(Object source, String msg) {
								// TODO Auto-generated method stub

							}

							@Override
							public void debug(Object source, String msg) {
								// TODO Auto-generated method stub

							}
						};
					}

					@Override
					public Logger getLogger(String name) {
						// TODO Auto-generated method stub
						return new Logger() {

							@Override
							public boolean isInfo() {
								// TODO Auto-generated method stub
								return false;
							}

							@Override
							public boolean isError() {
								// TODO Auto-generated method stub
								return false;
							}

							@Override
							public boolean isDebug() {
								// TODO Auto-generated method stub
								return false;
							}

							@Override
							public void info(Object source, String msg) {
								// TODO Auto-generated method stub

							}

							@Override
							public void error(Object source, String msg) {
								// TODO Auto-generated method stub

							}

							@Override
							public void debug(Object source, String msg) {
								// TODO Auto-generated method stub

							}
						};
					}
				});
		long startTime = System.currentTimeMillis();
		List<Data> datas = ftpFileInPutStreamFactory.splitToData(5);
		for (Data data : datas) {
			FTPClient ftpClient = FTPUtils.getClient(ftpConfig.getHost(), ftpConfig.getPort(), ftpConfig.getUserName(),
					ftpConfig.getPassword(), ftpConfig.getEncoding());
			ftpClient.retrieveFile(((FTPFileInPutStreamFactory.FTPFileEntiry) (data)).getAbsPath(),
					new FileOutputStream(
							"F:\\tmp\\new\\" + ((FTPFileInPutStreamFactory.FTPFileEntiry) (data)).getAbsPath()));
		}
		long endTime = System.currentTimeMillis();
		System.out.println((endTime-startTime)/1000);
	}
}
