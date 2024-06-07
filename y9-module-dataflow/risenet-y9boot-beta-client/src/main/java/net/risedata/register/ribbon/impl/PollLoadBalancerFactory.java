package net.risedata.register.ribbon.impl;

import net.risedata.register.ribbon.LBalancer;

/**
 * 轮询算法负载均衡工厂
 */
public class PollLoadBalancerFactory extends AbstractLoadBalancerFactory {

    public PollLoadBalancerFactory() {
    }

    @Override
    LBalancer createInstance(String name) {
        return new PollBalancer();
    }
}
