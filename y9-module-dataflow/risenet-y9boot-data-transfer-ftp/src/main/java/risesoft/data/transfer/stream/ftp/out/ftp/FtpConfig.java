package risesoft.data.transfer.stream.ftp.out.ftp;

import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;
import risesoft.data.transfer.stream.ftp.model.FTPInfo;
import risesoft.data.transfer.stream.ftp.utils.FTPUtils;

/**
 * ftp配置
 * 
 * 
 * @typeName FtpConfig
 * @date 2024年12月17日
 * @author lb
 */
@ConfigBean
public class FtpConfig extends FTPInfo{
	@ConfigField(description = "host", required = true)
	private String host;
	@ConfigField(description = "端口", required = true)
	private int port;
	@ConfigField(description = "用户名", required = true)
	private String userName;
	@ConfigField(description = "密码", required = true)
	private String password;
	@ConfigField(description = "路径", required = true)
	private String path;
	@ConfigField(description = "日志名字", value = "FtpFileIn")
	private String name;
	@ConfigField(description = "编码", value = FTPUtils.DEFAULT_ENCODING)
	private String encoding;
	@ConfigField(description = "主动模式", value = "false", required = false)
	private boolean activeModel;
	@ConfigField(description = "缓存大小/kb", value = "1024")
	private int bufferSize;
	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public String getPath() {
		return path;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	

	public String getName() {
		return name;
	}

	public String getEncoding() {
		return encoding;
	}

	public boolean isActiveModel() {
		return activeModel;
	}

}
