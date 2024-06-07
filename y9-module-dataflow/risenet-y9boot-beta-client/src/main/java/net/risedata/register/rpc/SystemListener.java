package net.risedata.register.rpc;

import net.risedata.register.container.ContainerManager;
import net.risedata.register.model.Const;
import net.risedata.rpc.consumer.annotation.Listener;
import net.risedata.rpc.consumer.annotation.Listeners;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Description : 监控一些系统操作
 * @ClassName SystemListener
 * @Author lb
 * @Date 2021/12/22 17:39
 * @Version 1.0
 */
@Listeners
public class SystemListener {

    @Autowired(required = false)
    ContainerManager containerManager;

    @Listener(value = Const.SYS_RESTART)
    public void reStart() {
        if (containerManager != null) {
            containerManager.reStart();
        }
    }

    @Listener(value = Const.SYS_STOP)
    public void stop() {
        if (containerManager != null) {
            containerManager.stop();
        }
    }
}
