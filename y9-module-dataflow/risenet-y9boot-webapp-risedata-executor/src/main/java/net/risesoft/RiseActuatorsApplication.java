package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.risedata.rpc.annotation.RPCScan;
import net.risesoft.y9.spring.boot.Y9Banner;

import risesoft.data.transfer.assembler.Initialization;
import risesoft.data.transfer.core.start.StartConfiguration;

@SpringBootApplication
@RPCScan("net.risesoft.api")
@EnableScheduling
public class RiseActuatorsApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws Exception {
        Initialization.install(StartConfiguration.class);
        SpringApplication springApplication = new SpringApplication(RiseActuatorsApplication.class);
        springApplication.setBanner(new Y9Banner());
        springApplication.run(args);
    }
}
