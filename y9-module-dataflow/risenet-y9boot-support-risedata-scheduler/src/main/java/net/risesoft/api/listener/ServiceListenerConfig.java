package net.risesoft.api.listener;


import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description : 登记监控信息配置
 * @ClassName ServiceListenerConfig
 * @Author lb
 * @Date 2022/4/7 16:56
 * @Version 1.0
 */
@Component
public class ServiceListenerConfig  implements ApplicationListener<ApplicationStartedEvent> {



    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        Map<String, ServiceListener> serviceListeners = event.getApplicationContext().getBeansOfType(ServiceListener.class);
        if (serviceListeners!=null&&serviceListeners.size()>0){
            for (ServiceListener serviceListener : serviceListeners.values()) {
//                InstanceManager.addListener(serviceListener);
            }
        }
    }
}
