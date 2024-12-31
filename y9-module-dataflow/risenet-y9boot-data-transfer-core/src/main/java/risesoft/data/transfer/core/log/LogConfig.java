package risesoft.data.transfer.core.log;

import org.apache.commons.lang3.StringUtils;

import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;

/**
 * 给予其他配置类作为父类,用于设置日志类的config,
 * 使用logName作为日志字段,将内部log对象作为日志对象,如未配置logName,则自动采用当前类的名字作为日志名
 * 
 * 
 * @typeName LogConfig
 * @date 2024年12月31日
 * @author lb
 */
@ConfigBean
public class LogConfig {
	/**
	 * 自带日志
	 */
	protected Logger log;

	@ConfigField(required = false)
	protected String logName;

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName, LoggerFactory loggerFactory) {
		if (StringUtils.isEmpty(logName)) {
			this.logName = this.getClass().getName();
		}
		this.log = loggerFactory.getLogger(logName);
	}

	public Logger getLog() {
		return log;
	}

}
