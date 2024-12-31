package risesoft.data.transfer.stream.rdbms.plug;

import risesoft.data.transfer.core.context.JobContext;
import risesoft.data.transfer.core.factory.annotations.ConfigBean;
import risesoft.data.transfer.core.factory.annotations.ConfigField;
import risesoft.data.transfer.core.log.LogConfig;
import risesoft.data.transfer.core.util.Configuration;
import risesoft.data.transfer.core.util.ConfigurationConst;

/**
 * 通过配置查找到sql配置源的插件
 * 
 * 
 * @typeName ConfigFinedExecutorSqlPlug
 * @date 2024年12月31日
 * @author lb
 */
public class ConfigFinedExecutorSqlPlug extends ExecutorSql {

	@ConfigBean
	class ExecutorConfig extends LogConfig {

		@ConfigField(required = true, description = "sql")
		private String sql;

		@ConfigField(required = false, value = "0", description = "目标源索引")
		private Integer index;

		@ConfigField(required = false, value = "out", description = "目标源", options = { "out", "in" })
		private String source;

		public String getSql() {
			return sql;
		}
	}

	protected ExecutorConfig config;

	public ConfigFinedExecutorSqlPlug(ExecutorConfig config, JobContext jobContext) {
		super();
		Configuration configuration = jobContext.getConfiguration();
		configuration = configuration.getListConfiguration(ConfigurationConst.JOBS).get(config.index)
				.getConfiguration(config.source).getConfiguration("args");
		this.jdbcPassword = configuration.getString("password");
		this.jdbcUrl = configuration.getString("jdbcUrl");
		this.jdbcUserName = configuration.getString("userName");
		this.config = config;
		if (config.getLog().isDebug()) {
			config.getLog().debug(this, "find configuration:" + configuration);
		}
	}

}
