package net.risesoft.api.listener;

import net.risedata.register.service.IServiceInstance;

/**
 * @Description : 监控服务变动信息
 * @ClassName ServiceListener
 * @Author lb
 * @Date 2022/4/7 15:57
 * @Version 1.0
 */
public interface ServiceListener {
    /**
     * 当有服务停止的时候触发
     * @param iServiceInstance
     */
    void onStop(IServiceInstance iServiceInstance);

    /**
     * 当有服务启动的时候触发
     * @param iServiceInstance
     */
    void onStart(IServiceInstance iServiceInstance);
}
