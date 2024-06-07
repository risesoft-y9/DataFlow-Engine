package net.risedata.register.container.aas;

import java.io.File;

import net.risedata.register.container.SystemContainerManager;
import net.risedata.register.system.operation.SystemOperation;
import net.risedata.register.system.operation.linux.LinuxSystemOperation;
import net.risedata.register.system.operation.windows.WindowsSystemOperation;

/**
 * @Description : 金蝶v10 容器管理器
 * @ClassName AASV9ContainerManager
 * @Author lb
 * @Date 2021/12/22 15:23
 * @Version 1.0
 */
public class AASV10ContainerManager extends SystemContainerManager {
    /**
     * root path 应是当前系统的域根目录
     *
     * @param systemOperation
     * @param rootPath
     */
    public AASV10ContainerManager(SystemOperation systemOperation, String rootPath) {
        super(systemOperation, rootPath);
        LOGGER.info("rootPath: "+rootPath);
    }

    private static String DOMAINS = "/AAMS-V10";

    public static void main(String[] args) {
        AASV10ContainerManager.create("/opt/aas10/AAMS-V102/bin",new WindowsSystemOperation());
    }
    /**
     * 创建当前容器管理器
     *
     * @return
     */
    public static AASV10ContainerManager create(String contextPath, SystemOperation systemOperation) {
        contextPath = contextPath.replace("\\", "/");
        int domainsIndex = contextPath.indexOf(DOMAINS);

        if (domainsIndex != -1) {
            domainsIndex = contextPath.indexOf("/", domainsIndex + DOMAINS.length());
            if (domainsIndex != -1) {
                String path = contextPath.substring(0,domainsIndex);
                System.out.println("执行指令"+path + "/bin/apusic.sh run ");
                if (new File(path + "/bin").exists()) {
                    if (systemOperation instanceof WindowsSystemOperation){
                        path = path.substring(1);
                    }
                    System.out.println("create AASV10");
                    return new AASV10ContainerManager(systemOperation, path);
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
                systemOperation.execute(true,"cd " + rootPath + "/bin", systemOperation.nohupRun(rootPath + "/bin/apusic.sh run >>nohup.out "));
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
