package net.risedata.register.container;

import net.risedata.register.system.operation.SystemOperation;

/**
 * @Description : 未知的容器
 * @ClassName UnknownContainerManager
 * @Author lb
 * @Date 2021/12/23 9:41
 * @Version 1.0
 */
public class UnknownContainerManager extends SystemContainerManager {
    public UnknownContainerManager(SystemOperation systemOperation, String rootPath) {
        super(systemOperation, rootPath);
    }




    @Override
    public void start() {
        LOGGER.error("unknown container");
    }
}
