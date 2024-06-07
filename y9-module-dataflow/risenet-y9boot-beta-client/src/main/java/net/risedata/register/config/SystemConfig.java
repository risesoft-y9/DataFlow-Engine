package net.risedata.register.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import net.risedata.register.container.ContainerManager;
import net.risedata.register.container.UnknownContainerManager;
import net.risedata.register.container.aas.AASV10ContainerManager;
import net.risedata.register.container.aas.AASV9ContainerManager;
import net.risedata.register.container.jar.JARContainerManager;
import net.risedata.register.container.tomcat.TomcatContainerManager;
import net.risedata.register.rpc.SystemListener;
import net.risedata.register.system.operation.SystemOperation;
import net.risedata.register.system.operation.linux.LinuxSystemOperation;
import net.risedata.register.system.operation.windows.WindowsSystemOperation;

/**
 * @Description : 用于系统操作 关机重启
 * @ClassName SystemConfig
 * @Author lb
 * @Date 2021/12/22 17:45
 * @Version 1.0
 */
//@Configuration
public class SystemConfig {


    @Bean
    @ConditionalOnMissingBean(SystemOperation.class)
    public SystemOperation createSystemOperation() {
        String os = System.getProperty("os.name");
        if (os.contains("Windows")) {
            return new WindowsSystemOperation();
        } else {
            return new LinuxSystemOperation();
        }
    }

    @Bean
    @ConditionalOnMissingBean(ContainerManager.class)
    public ContainerManager containerManager(SystemOperation systemOperation) {
        Class<?> rootClass = SystemConfigSelector.rootClass;
        String rootPath = rootClass.getResource("").getPath().replace("file:/","");

        ContainerManager containerManager = JARContainerManager.create(rootPath, systemOperation);
        if (containerManager == null) {
            containerManager = TomcatContainerManager.create(rootPath, systemOperation);
        }
        if (containerManager == null) {
            containerManager = AASV9ContainerManager.create(rootPath, systemOperation);
        }
        if (containerManager == null) {
            containerManager = AASV10ContainerManager.create(rootPath, systemOperation);
        }
        return containerManager == null ? new UnknownContainerManager(systemOperation, rootPath) : containerManager;
    }



    @Bean
    SystemListener systemListener() {
        return new SystemListener();
    }
}
