package risesoft.data.transfer.stream.out.stream;

import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;

/***
 * 本地文件配置
 * 
 * @typeName LocalFileConfig
 * @date 2024年3月1日
 * @author lb
 */
@ConfigBean
public class LocalFileConfig {

	@ConfigField(required = true, description = "根路径")
	protected String rootPath;
	@ConfigField(description = "名字", value = "LocalFile")
	protected String name;

	public String getRootPath() {
		return rootPath;
	}

	public String getName() {
		return name;
	}

}