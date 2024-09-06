package net.risesoft;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

import net.risedata.rpc.annotation.RPCScan;
import risesoft.data.transfer.assembler.Initialization;
import risesoft.data.transfer.core.start.StartConfiguration;

@SpringBootApplication
@RPCScan("net.risesoft.api")
@EnableScheduling
public class RiseActuatorsApplication extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {
		Initialization.install(StartConfiguration.class);
		SpringApplication.run(RiseActuatorsApplication.class, args);

	}
}
