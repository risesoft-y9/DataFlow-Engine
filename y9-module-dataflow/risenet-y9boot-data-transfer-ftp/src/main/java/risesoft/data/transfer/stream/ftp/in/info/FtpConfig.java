package risesoft.data.transfer.stream.ftp.in.info;

import risesoft.data.transfer.core.data.Data;
import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;
import risesoft.data.transfer.stream.ftp.model.FTPInfo;
import risesoft.data.transfer.stream.ftp.utils.FTPUtils;

@ConfigBean
public class FtpConfig extends FTPInfo implements Data {
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

	@ConfigField(description = "文件名", required = false)
	private String fileName;
	@ConfigField(description = "时间", required = false)
	private String date;
	@ConfigField(description = "主动模式",value = "false", required = false)
	private boolean activeModel;

	public FtpConfig() {
		super();
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

	public String getFileName() {
		return fileName;
	}

	
	
	public boolean isActiveModel() {
		return activeModel;
	}

	public void setActiveModel(boolean activeModel) {
		this.activeModel = activeModel;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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