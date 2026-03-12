package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.spring.boot.Y9Banner;

@SpringBootApplication
@EnableConfigurationProperties(Y9Properties.class)
@EnableScheduling
public class RiseLogApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RiseLogApplication.class);
        springApplication.setBanner(new Y9Banner());
        springApplication.run(args);
    }
}
