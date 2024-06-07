package net.risedata.register.watch.impl;

import net.risedata.register.watch.ServiceAvailability;
import net.risedata.rpc.provide.config.Application;

/**
 * @Description : 监控数据库
 * @ClassName DatabaseAvailability
 * @Author lb
 * @Date 2022/4/7 10:06
 * @Version 1.0
 */
public class RuntimeAvailability implements ServiceAvailability {

    private Runnable runnable;

    public RuntimeAvailability(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public boolean isAvailability() {
        try {
            Application.logger.debug("check runnable:  ");
            runnable.run();
            return true;
        } catch (Exception e) {
            Application.logger.error("error : "+e.getMessage());
            return false;
        }
    }


}
