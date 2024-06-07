package net.risedata.register.ribbon.impl;

import net.risedata.register.ribbon.LBalancer;

/**
 * 加权随机负载均衡算法工厂
 */
public class WeightRandomLoadBalancerFactory extends AbstractLoadBalancerFactory {



    @Override
    LBalancer createInstance(String name) {
        return new WeightRandomBalancer();
    }
}
