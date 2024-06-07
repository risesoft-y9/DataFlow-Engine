package net.risedata.register.container.tomcat;


import net.risedata.register.container.ContainerManager;
import net.risedata.register.container.SystemContainerManager;
import net.risedata.register.system.operation.SystemOperation;
import net.risedata.register.system.operation.linux.LinuxSystemOperation;

/**
 * @Description : tomcat 容器管理器
 * @ClassName TomcatContainerManager
 * @Author lb
 * @Date 2021/12/22 15:24
 * @Version 1.0
 */
public class TomcatContainerManager extends SystemContainerManager {


    public TomcatContainerManager(SystemOperation systemOperation, String rootPath) {
        super(systemOperation, rootPath);
    }

    public static ContainerManager create(String rootPath, SystemOperation systemOperation) {
        return null;
    }


    @Override
    public void start() {
        if (systemOperation instanceof LinuxSystemOperation) {
            systemOperation.nohupRun(rootPath + "/bin/startup.sh");
        } else {
            systemOperation.nohupRun(rootPath + "/bin/startup.bat");
        }

    }


}
