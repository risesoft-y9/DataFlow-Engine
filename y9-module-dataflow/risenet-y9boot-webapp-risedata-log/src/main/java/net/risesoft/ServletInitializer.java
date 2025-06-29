package net.risesoft;

import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;
import org.springframework.web.context.WebApplicationContext;

public class ServletInitializer extends SpringBootServletInitializer {
	
	@Override
	protected WebApplicationContext run(SpringApplication application) {
		WebApplicationContext ctx = super.run(application);
		Environment env = ctx.getEnvironment();
		String sessionTimeout = env.getProperty("server.servlet.session.timeout", "300");
		String cookieSecure = env.getProperty("server.servlet.session.cookie.secure", "false");

		ServletContext servletContext = ctx.getServletContext();
		servletContext.setSessionTrackingModes(Collections.singleton(SessionTrackingMode.COOKIE));
		servletContext.setSessionTimeout(Integer.valueOf(sessionTimeout));
		SessionCookieConfig sessionCookieConfig = servletContext.getSessionCookieConfig();
		sessionCookieConfig.setHttpOnly(true);
		sessionCookieConfig.setSecure(Boolean.valueOf(cookieSecure));
		return ctx;
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		setRegisterErrorPageFilter(false);
		builder.sources(RiseLogApplication.class);
		return builder;
	}
}
