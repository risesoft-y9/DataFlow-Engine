package net.risedata.register.ribbon.impl;

import net.risedata.register.ribbon.LBalancer;

/**
 * @Description : 单例
 * @ClassName SingleLoadBalancerFactory
 * @Author lb
 * @Date 2021/11/30 14:52
 * @Version 1.0
 */
public class SingleLoadBalancerFactory extends AbstractLoadBalancerFactory {

    private LBalancer balancer;

    public SingleLoadBalancerFactory(LBalancer balancer) {
        this.balancer = balancer;
    }

    @Override
    LBalancer createInstance(String name) {
        return balancer;
    }
}
