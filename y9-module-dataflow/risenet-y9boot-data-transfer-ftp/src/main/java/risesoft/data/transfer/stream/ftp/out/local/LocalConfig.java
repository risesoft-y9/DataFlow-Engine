package risesoft.data.transfer.stream.ftp.out.local;

import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;

@ConfigBean
public class LocalConfig {

	@ConfigField(description = "路径", required = true)
	private String path;
	@ConfigField(description = "日志名字", value = "FTPFileOutLocal")
	private String name;

	@ConfigField(description = "缓存大小/kb", value = "1024")
	private int bufferSize;
	public LocalConfig() {
		super();
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
	
	

}