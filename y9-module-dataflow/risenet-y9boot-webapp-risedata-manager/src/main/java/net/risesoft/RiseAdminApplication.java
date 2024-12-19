package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.risesoft.y9.configuration.Y9Properties;

@SpringBootApplication
@EnableConfigurationProperties(Y9Properties.class)
@EnableScheduling
public class RiseAdminApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(RiseAdminApplication.class, args);
	}
}
