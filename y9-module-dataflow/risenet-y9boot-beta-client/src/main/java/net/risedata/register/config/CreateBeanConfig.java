package net.risedata.register.config;

import com.netflix.loadbalancer.IRule;

import net.risedata.register.api.filter.ListenerFilter;
import net.risedata.register.api.filter.listener.CountListener;
import net.risedata.register.api.system.FileOperation;
import net.risedata.register.api.system.SystemOperation;
import net.risedata.register.discover.RegisterDiscoveryClient;
import net.risedata.register.model.WatchProperties;
import net.risedata.register.ribbon.LRule;
import net.risedata.register.ribbon.LoadBalancerFactory;
import net.risedata.register.ribbon.impl.PollLoadBalancerFactory;
import net.risedata.register.rpc.RegisterListener;
import net.risedata.register.watch.ServiceWatchController;
import net.risedata.register.watch.impl.CPUServiceAvailability;
import net.risedata.register.watch.impl.MEMServiceAvailability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description :
 * @ClassName CreateBeanConfig
 * @Author lb
 * @Date 2021/11/26 14:42
 * @Version 1.0
 */
@Configuration
@AutoConfigureOrder(0)
@AutoConfigureBefore({ SimpleDiscoveryClientAutoConfiguration.class, CommonsClientAutoConfiguration.class })
@EnableScheduling
//@ConditionalOnProperty(value = "spring.cloud.service-registry.auto-registration.enabled", matchIfMissing = true)
public class CreateBeanConfig {

	private static Logger LOGGER = LoggerFactory.getLogger(CreateBeanConfig.class);

	@Bean
	public SystemOperation getSystemOperation() {
		return new SystemOperation();
	}

	@Bean
	@ConditionalOnMissingBean(RegisterDiscoveryClient.class)
	public DiscoveryClient discoveryClient() {
		return new RegisterDiscoveryClient(serverAddr, environment);
	}

	@Bean
	@ConditionalOnProperty(name = "beta.discovery.fileOperation", matchIfMissing = false)
	public FileOperation getFileOperation() {
		return new FileOperation();
	}

	@Bean
	@ConditionalOnProperty(name = "beta.discovery.listener", matchIfMissing = false)
	public FilterRegistrationBean filterRegistration(ListenerFilter listenerFilter) {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(listenerFilter);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	CountListener countListener() {
		return new CountListener();
	}

	@Bean
	@ConditionalOnProperty(name = "beta.discovery.listener", matchIfMissing = false)
	public ListenerFilter filter() {
		LOGGER.info("start listener");
		return new ListenerFilter();
	}

	@Bean
	public RPCConfig getRPCConfig() {
		return new RPCConfig();
	}

	@Bean
	@Scope(value = "prototype")
	@ConditionalOnMissingBean(IRule.class)
	public LRule myRule() {
		return new LRule();
	}

	@Bean
	@ConditionalOnMissingBean(LoadBalancerFactory.class)
	public LoadBalancerFactory getPollLoadBalancerFactory() {
		return new PollLoadBalancerFactory();
	}

	@Bean
	public RegisterListener getRegisterListener() {
		return new RegisterListener();
	}

	@Bean
	public WatchProperties WatchProperties() {
		return new WatchProperties();
	}

	@Value("${beta.discovery.serverAddr:}")
	private String serverAddr;
	@Value("${beta.discovery.environment:Public}")
	String environment;

	@Bean
	@ConditionalOnMissingBean(ServiceWatchController.class)
	@ConditionalOnProperty(name = "beta.discovery.watch.watch", havingValue = "true")
	public ServiceWatchController watchController() {
		return new ServiceWatchController();
	}

	@Bean
	@ConditionalOnProperty(name = "beta.discovery.watch.cpuError", matchIfMissing = false)
	public CPUServiceAvailability getCPUServiceAvailability() {
		return new CPUServiceAvailability();
	}

	@Bean
	@ConditionalOnProperty(name = "beta.discovery.watch.memError", matchIfMissing = false)
	public MEMServiceAvailability getMEMServiceAvailability() {
		return new MEMServiceAvailability();
	}

}
