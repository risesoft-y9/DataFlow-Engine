package net.risesoft.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
	
	@Bean
	public ModelMapper modelMapper() {
		ModelMapper modelMapper = new ModelMapper();
		// 配置严格匹配策略，确保为null的Integer类型字段在转换时不会变为0
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT).
			setPropertyCondition(context -> context.getSource() != null);
	    return modelMapper;
	}

}
