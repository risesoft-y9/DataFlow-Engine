package net.risedata.register.ribbon;

/**
 * @Description : 负责均衡算法工厂
 * @InterfaceName LoadBalancerFactory
 * @Author lb
 * @Date 2021/11/30 14:36
 * @Version 1.0
 */
public interface LoadBalancerFactory {


    /**
     * 根据实例名获取一个负载均衡实例
     * @param name 实例名
     * @return 实例
     */
      LBalancer selectorBalancer(String name) ;
}
