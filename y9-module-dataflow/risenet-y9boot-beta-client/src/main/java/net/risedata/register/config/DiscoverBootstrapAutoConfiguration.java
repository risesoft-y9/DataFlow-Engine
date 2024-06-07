package net.risedata.register.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risedata.register.discover.RegisterDiscoveryClient;

/**
 * @Description : 服务注册与发现配置类
 * @ClassName DiscoverAutoConfiguration
 * @Author lb
 * @Date 2021/11/25 9:42
 * @Version 1.0
 */
@Configuration
@AutoConfigureOrder(0)
@AutoConfigureBefore({SimpleDiscoveryClientAutoConfiguration.class,
        CommonsClientAutoConfiguration.class})
//@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled", matchIfMissing = true)
public class DiscoverBootstrapAutoConfiguration {




    /**
     *  discovery server address.
     */
    @Value("${beta.discovery.serverAddr:}")
    private String serverAddr;
    @Value("${beta.discovery.environment:Public}")
    String environment;

    @Bean
    @ConditionalOnProperty(value = "beta.discovery.serverAddr")
    public DiscoveryClient discoveryClient() {
        RegisterDiscoveryClient registerDiscoveryClient = new RegisterDiscoveryClient(serverAddr,environment);
        registerDiscoveryClient.refreshAll();
        return registerDiscoveryClient;
    }


}
