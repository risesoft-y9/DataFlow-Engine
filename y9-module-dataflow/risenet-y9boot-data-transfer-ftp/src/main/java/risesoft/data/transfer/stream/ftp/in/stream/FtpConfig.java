package risesoft.data.transfer.stream.ftp.in.stream;

import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;
import risesoft.data.transfer.stream.ftp.utils.FTPUtils;

@ConfigBean
public class FtpConfig {
	@ConfigField(description = "host", required = true)
	protected String host;
	@ConfigField(description = "端口", required = true)
	protected int port;
	@ConfigField(description = "用户名", required = true)
	protected String userName;
	@ConfigField(description = "密码", required = true)
	protected String password;
	@ConfigField(description = "路径", required = true)
	protected String path;
	/**
	 * 缓存区大小单位kb 默认为10kb
	 */
	@ConfigField(description = "缓存区", value = "10240")
	protected int buffer;
	@ConfigField(description = "日志名字", value = "FtpFileIn")
	protected String name;
	@ConfigField(description = "编码", value = FTPUtils.DEFAULT_ENCODING)
	protected String encoding;
	@ConfigField(description = "主动模式",value = "false", required = false)
	private boolean activeModel;
	

	public boolean isActiveModel() {
		return activeModel;
	}

	public void setActiveModel(boolean activeModel) {
		this.activeModel = activeModel;
	}
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getBuffer() {
		return buffer;
	}

	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

}