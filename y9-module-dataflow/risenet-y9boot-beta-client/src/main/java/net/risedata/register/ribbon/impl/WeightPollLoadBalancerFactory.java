package net.risedata.register.ribbon.impl;

import net.risedata.register.ribbon.LBalancer;

/**
 * 加权轮询负载均衡算法工厂
 */
public class WeightPollLoadBalancerFactory extends AbstractLoadBalancerFactory {



    @Override
    LBalancer createInstance(String name) {
        return new WeightPollBalancer();
    }
}
