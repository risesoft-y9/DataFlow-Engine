package net.risedata.register.ribbon.impl;

import java.util.HashMap;
import java.util.Map;

import net.risedata.register.ribbon.LBalancer;
import net.risedata.register.ribbon.LoadBalancerFactory;

/**
 * @Description :
 * @ClassName AbstractLoadBalancerFactory
 * @Author lb
 * @Date 2021/11/30 14:52
 * @Version 1.0
 */
public abstract class AbstractLoadBalancerFactory implements LoadBalancerFactory {

    protected Map<String, LBalancer> balancerMap = new HashMap<>();

    abstract LBalancer createInstance(String name);


    @Override
    public LBalancer selectorBalancer(String name) {
        LBalancer balancer = balancerMap.get(name);
        if (balancer == null) {
            synchronized (name.intern()) {
                balancer = balancerMap.get(name);
                if (balancer == null) {
                    balancer = createInstance(name);
                    balancerMap.put(name, balancer);
                }
            }
        }
        return balancer;
    }
}
