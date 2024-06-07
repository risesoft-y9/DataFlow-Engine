package net.risedata.register.ribbon;

import com.netflix.loadbalancer.Server;
import org.springframework.cloud.client.ServiceInstance;

/**
 * @Description : 继承ribbon的server用于负载均衡
 * @ClassName MyServer
 * @Author lb
 * @Date 2021/11/25 14:13
 * @Version 1.0
 */
public class LServer extends  Server {

    public LServer(ServiceInstance instance) {
        super(instance.getHost(), instance.getPort());
    }
}
