package risesoft.data.transfer.core.data;

import java.util.List;

import risesoft.data.transfer.core.util.Configuration;

public class ListConfigurationData implements Data{

	private List<Configuration> configurations;

	public ListConfigurationData(List<Configuration> configurations) {
		super();
		this.configurations = configurations;
	}

	public List<Configuration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<Configuration> configurations) {
		this.configurations = configurations;
	}
	
	@Override
	public String toString() {
		
		return this.configurations.toString();
	}
}
