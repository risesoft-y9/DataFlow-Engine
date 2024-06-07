package risesoft.data.transfer.core.data;

import risesoft.data.transfer.core.util.Configuration;

public class ConfigurationData implements Data{

	private Configuration configuration;

	public ConfigurationData(Configuration configuration) {
		super();
		this.configuration = configuration;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	
}
