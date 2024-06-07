package net.risedata.register.ribbon;


import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @Description : 负载均衡算法器
 * @ClassName LBalancer
 * @Author lb
 * @Date 2021/11/30 14:42
 * @Version 1.0
 */
public interface LBalancer {
    /**
     * 选择一个实例
     * @param services
     * @return
     */
    ServiceInstance choose(List<ServiceInstance> services);

}
