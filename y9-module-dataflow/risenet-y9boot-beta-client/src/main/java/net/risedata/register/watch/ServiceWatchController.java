package net.risedata.register.watch;

import net.risedata.register.model.Const;
import net.risedata.register.model.WatchProperties;
import net.risedata.register.system.Server;
import net.risedata.register.watch.impl.SystemAvailability;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description : 监控信息服务
 * @ClassName ServiceWatchController
 * @Author lb
 * @Date 2021/12/3 15:48
 * @Version 1.0
 */
@RestController
@RequestMapping(Const.WATCH_URL)
public class ServiceWatchController {

    @Autowired
    WatchProperties watchProperties;

    @Autowired(required = false)
    List<ServiceAvailability> serviceAvailability;

    /**
     * 检查服务信息用于服务端http调用返回的信息会根据监控的信息返回
     *
     * @return
     */
    @RequestMapping(Const.CHECK)
    public boolean checkServerInfo() {
        if (serviceAvailability != null) {
            for (ServiceAvailability availability : serviceAvailability) {
                if (!availability.isAvailability()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 此接口提供给客户端使用使用每次调用都会拿到最新的配置
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("getServerInfo")
    public Server getServerInfo() throws Exception {
        Server server = SystemAvailability.getServer();
        server.refreshCpu();
        server.refreshMem();
        return server;
    }

}
