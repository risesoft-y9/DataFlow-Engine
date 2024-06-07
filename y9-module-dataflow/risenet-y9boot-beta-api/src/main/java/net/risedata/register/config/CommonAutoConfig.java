package net.risedata.register.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risedata.register.service.IServiceInstanceFactory;
import net.risedata.register.service.RegisterDiscoveryProperties;

/**
 * @Description :
 * @ClassName CommonAutoConfig
 * @Author lb
 * @Date 2021/12/6 9:38
 * @Version 1.0
 */
@Configuration
public class CommonAutoConfig {

    @Bean
    public RegisterDiscoveryProperties RegisterDiscoveryProperties() {

        return new RegisterDiscoveryProperties();
    }


    @Bean
    IServiceInstanceFactory getIServiceInstanceFactory() {
        return new IServiceInstanceFactory();
    }

}
