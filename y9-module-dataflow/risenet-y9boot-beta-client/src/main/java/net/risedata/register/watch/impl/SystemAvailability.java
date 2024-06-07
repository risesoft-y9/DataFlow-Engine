package net.risedata.register.watch.impl;

import net.risedata.register.exceptions.RegisterException;
import net.risedata.register.system.Cpu;
import net.risedata.register.system.Mem;
import net.risedata.register.system.Server;
import net.risedata.register.watch.ServiceAvailability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description : 保存着系统信息
 * @ClassName SystemAvailability
 * @Author lb
 * @Date 2021/12/6 10:21
 * @Version 1.0
 */
public abstract class SystemAvailability implements ServiceAvailability {

    private static Server SERVER;

    protected static final Logger LOGGER = LoggerFactory.getLogger(SystemAvailability.class);

    public static Server getServer() {
        if (SERVER == null) {
            synchronized (SystemAvailability.class) {
                if (SERVER == null) {
                    SERVER = new Server();
                    try {
                        SERVER.serverInfo();
                    } catch (Exception e) {
                        throw new RegisterException(e.getMessage());
                    }
                }
            }
        }
        return SERVER;
    }

    public static Cpu getConcurrentCpu() {
        getServer().refreshCpu();
        return getServer().getCpu();
    }

    public static Mem getConcurrentMem() {
        getServer().refreshMem();
        return getServer().getMem();
    }
}
