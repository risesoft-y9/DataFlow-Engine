package net.risedata.register.container;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.risedata.register.system.operation.SystemOperation;

/**
 * @Description : 提供系统操作的容器管理器
 * @ClassName SystemContainerManager
 * @Author lb
 * @Date 2021/12/22 15:25
 * @Version 1.0
 */
public abstract class SystemContainerManager implements ContainerManager {

    public static final Logger LOGGER = LoggerFactory.getLogger(SystemContainerManager.class);

    protected SystemOperation systemOperation;
    protected String rootPath;

    public SystemContainerManager(SystemOperation systemOperation, String rootPath) {
        this.systemOperation = systemOperation;
        this.rootPath = rootPath;
    }


    @Override
    public void reStart() {
        SystemContainerManager systemContainerManager = this;
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println( systemContainerManager.getClass());
                    systemContainerManager.start();
                } catch (Exception e) {
                    System.out.println("启动报错22---");
                    e.printStackTrace();

                }
            }
        });
        stop();
    }

    public void stop() {
        System.exit(-1);
    }
}
