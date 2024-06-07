package net.risedata.register.container.jar;


import java.io.File;

import net.risedata.register.container.SystemContainerManager;
import net.risedata.register.system.operation.SystemOperation;

/**
 * @Description : jar 包方式容器管理器
 * @ClassName JARContainerManager
 * @Author lb
 * @Date 2021/12/22 15:23
 * @Version 1.0
 */
public class JARContainerManager extends SystemContainerManager {

    private String jarName;

    /**
     * rootpath 应是自己的jar目录
     *
     * @param systemOperation
     * @param rootPath
     */
    public JARContainerManager(SystemOperation systemOperation, String rootPath, String jarName) {
        super(systemOperation, rootPath);
        this.jarName = jarName;
        LOGGER.info("rootPath: "+rootPath+",jar name"+jarName);

    }

    public static JARContainerManager create(String rootPath, SystemOperation systemOperation) {
        rootPath = rootPath.replace("\\", "/");
        int jarPath = rootPath.indexOf(".jar");
        if (jarPath != -1) {
            int separateIndex = rootPath.substring(0, jarPath).lastIndexOf("/");
            if (separateIndex != -1) {
                String path = rootPath.substring(0, separateIndex + 1);
                String jarName = rootPath.substring(separateIndex + 1, jarPath + 4);
                System.out.println(path + jarName);

                if (new File(path + jarName).exists()) {

                    return new JARContainerManager(systemOperation, path, jarName);
                }
                return null;
            }
        }
        return null;
    }


    @Override
    public void start() {
        System.out.println("启动");
        systemOperation.execute(false,"cd " + rootPath, systemOperation.nohupRun("java -jar " + jarName));

    }


}
