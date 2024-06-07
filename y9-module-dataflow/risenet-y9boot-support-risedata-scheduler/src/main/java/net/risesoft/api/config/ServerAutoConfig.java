package net.risesoft.api.config;

import net.risedata.jdbc.config.EnableRepository;
import net.risedata.rpc.annotation.RPCScan;
import net.risesoft.api.api.RegisterApi;
import net.risesoft.api.config.model.ServiceConfigModel;
import net.risesoft.api.consumer.ServerRegisterListener;
import net.risesoft.api.listener.ClientListener;
import net.risesoft.api.listener.ServiceListenerConfig;
import net.risesoft.api.message.InstanceMessage;
import net.risesoft.api.message.JobMessage;
import net.risesoft.api.message.MessageService;
import net.risesoft.api.message.impl.DefaultInstanceMessage;
import net.risesoft.api.message.impl.DefaultJobMessage;
import net.risesoft.api.message.impl.DefaultMessageService;
import net.risesoft.api.message.impl.EmailServiceExecutor;
import net.risesoft.api.persistence.iservice.IServiceService;
import net.risesoft.api.persistence.iservice.impl.DataBaseIServiceService;
import net.risesoft.api.persistence.security.DefaultSecurityManager;
import net.risesoft.api.watch.WatchManager;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description :
 * @ClassName AutoConfig
 * @Author lb
 * @Date 2021/11/25 16:49
 * @Version 1.0
 */
@RPCScan({ "net.risesoft.api.api" })
@Configuration
@Configurable
@EnableRepository("net.risesoft.api.persistence.dao")
@ComponentScans(value = {
		@ComponentScan({ "net.risesoft.api.persistence.**", "net.risesoft.api.aop", "net.risesoft.api.config.**",
				"net.risesoft.api.job.**", "net.risesoft.api.security.**"  }),
		@ComponentScan(value = { "net.risedata.**" }) })
@EnableScheduling
//@EnableWebFlux
public class ServerAutoConfig {

	@Bean
	@ConditionalOnMissingBean(RegisterApi.class)
	public RegisterApi registerApi() {
		return new RegisterApi();
	}

	@Bean(name = "securityManager")
	public FilterRegistrationBean filterRegistration(DefaultSecurityManager securityManager) {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(securityManager);
		registrationBean.addUrlPatterns("/*");
		registrationBean.setOrder(1);
		return registrationBean;
	}

	@Bean
	public ServiceConfigModel serviceConfigModel() {
		return new ServiceConfigModel();
	}

	@Bean
	@ConditionalOnMissingBean(IServiceService.class)
	public DataBaseIServiceService dataBaseIServiceService() {
		return new DataBaseIServiceService();
	}

	@Bean
	@ConditionalOnMissingBean(MessageService.class)
	public MessageService messageService() {
		return new DefaultMessageService();
	}

	@Bean
	@ConditionalOnMissingBean(InstanceMessage.class)
	public InstanceMessage instanceMessage() {
		return new DefaultInstanceMessage();
	}

	@Bean
	@ConditionalOnMissingBean(JobMessage.class)
	public JobMessage jobMessage() {
		return new DefaultJobMessage();
	}

	@Bean
	@ConditionalOnProperty(name = "spring.mail.username", matchIfMissing = false)
	public EmailServiceExecutor emailServiceExecutor() {
		return new EmailServiceExecutor();
	}

	@Bean
	public ClientListener clientListener() {
		return new ClientListener();
	}

	@Bean
	public ServerRegisterListener getServerRegisterListener() {
		return new ServerRegisterListener();
	}

	@Bean
	WatchManager getWatchManager() {
		return new WatchManager();
	}

	@Bean
	ServiceListenerConfig getServiceListenerConfig() {
		return new ServiceListenerConfig();
	}

}
