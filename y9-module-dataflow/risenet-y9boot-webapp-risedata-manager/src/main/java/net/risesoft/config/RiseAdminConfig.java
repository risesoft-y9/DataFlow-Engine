package net.risesoft.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.filter.OrderedRequestContextFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.RequestContextFilter;

import net.risesoft.converter.EncryptConverter;
import net.risesoft.y9.Y9Context;

@Configuration
@EnableAsync
public class RiseAdminConfig {

	// starter-log工程用到了RequestContextHolder
	// https://github.com/spring-projects/spring-boot/issues/2637
	// https://github.com/spring-projects/spring-boot/issues/4331
	@Bean
	public static RequestContextFilter requestContextFilter() {
		return new OrderedRequestContextFilter();
	}

	@Bean
	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
		supportedMediaTypes.add(MediaType.parseMediaType("text/html;charset=UTF-8"));
		// supportedMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
		converter.setSupportedMediaTypes(supportedMediaTypes);
		return converter;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public FilterRegistrationBean checkUserLoginFilter() {
		FilterRegistrationBean filterBean = new FilterRegistrationBean();
		filterBean.setFilter(new CheckUserLoginFilter());
		filterBean.setAsyncSupported(false);
		filterBean.setOrder(50);
		filterBean.addUrlPatterns("/api/*");
		return filterBean;
	}
	
	@Bean
	@DependsOn("y9Context")
	public EncryptConverter encryptConverter() {
		return new EncryptConverter();
	}

	@Bean
	public Y9Context y9Context() {
		return new Y9Context();
	}
	
}
