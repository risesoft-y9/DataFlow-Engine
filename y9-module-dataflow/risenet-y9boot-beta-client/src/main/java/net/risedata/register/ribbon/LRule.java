package net.risedata.register.ribbon;


import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Scope;

import java.util.List;


/**
 * @Description : 进行ribbon 整合
 * @ClassName MyRule
 * @Author lb
 * @Date 2021/11/25 11:37
 * @Version 1.0
 */
@Scope(value="prototype")
public class LRule extends RandomRule {

    @Autowired
    DiscoveryClient dis;

    @Autowired
    LoadBalancerFactory loadBalancerFactory;

    @Override
    public Server choose(Object key) {
        DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer) getLoadBalancer();
        String name = loadBalancer.getName();
        List<ServiceInstance> serviceInstanceList = dis.getInstances(name);
        if (serviceInstanceList.size() > 0) {
            return new LServer(loadBalancerFactory.selectorBalancer(name).choose(serviceInstanceList));
        }
        return null;
    }


}
