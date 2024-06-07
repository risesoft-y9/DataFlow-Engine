package net.risedata.register.container.aas;

import java.io.File;

import net.risedata.register.container.ContainerManager;
import net.risedata.register.container.SystemContainerManager;
import net.risedata.register.system.operation.SystemOperation;
import net.risedata.register.system.operation.linux.LinuxSystemOperation;
import net.risedata.register.system.operation.windows.WindowsSystemOperation;

/**
 * @Description : 金蝶v9 容器管理器
 * @ClassName AASV9ContainerManager
 * @Author lb
 * @Date 2021/12/22 15:23
 * @Version 1.0
 */
public class AASV9ContainerManager extends SystemContainerManager {
    /**
     * root path 应是当前系统的域根目录
     *
     * @param systemOperation
     * @param rootPath
     */
    public AASV9ContainerManager(SystemOperation systemOperation, String rootPath) {
        super(systemOperation, rootPath);
        LOGGER.info("rootPath: "+rootPath);
    }

    private static String DOMAINS = "/domains/";

    /**
     * 创建当前容器管理器
     *
     * @return
     */
    public static AASV9ContainerManager create(String contextPath, SystemOperation systemOperation) {
        contextPath = contextPath.replace("\\", "/");
        int domainsIndex = contextPath.indexOf(DOMAINS);
        System.out.println("金蝶路径: "+domainsIndex);
        if (domainsIndex != -1) {
            domainsIndex = contextPath.indexOf("/", domainsIndex + DOMAINS.length());
            if (domainsIndex != -1) {
                String path = contextPath.substring(0,domainsIndex);
                System.out.println("执行指令"+path + "/bin/startapusic");
                if (new File(path + "/bin").exists()) {
                    if (systemOperation instanceof WindowsSystemOperation){
                        path = path.substring(1);
                    }
                    return new AASV9ContainerManager(systemOperation, path);
                }
                return null;
            }
        }
        return null;
    }




    @Override
    public void start() {
        try {
            if (systemOperation instanceof LinuxSystemOperation) {
                systemOperation.execute(true,"cd " + rootPath + "/bin", systemOperation.nohupRun(rootPath + "/bin/startapusic >>nohup.out "));
            } else {
                systemOperation.execute(true,"cd " + rootPath + "/bin", systemOperation.nohupRun(rootPath + "/bin/startapusic.cmd"));
            }
        }catch ( Exception e){
            System.out.println("启动报错");
            e.printStackTrace();
        }

    }

    @Override
    public String toString() {
        return "AASV9ContainerManager{" +
                "systemOperation=" + systemOperation +
                ", rootPath='" + rootPath + '\'' +
                '}';
    }
}
