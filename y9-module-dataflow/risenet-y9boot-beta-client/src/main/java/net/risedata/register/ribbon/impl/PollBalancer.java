package net.risedata.register.ribbon.impl;

import org.springframework.cloud.client.ServiceInstance;

import net.risedata.register.ribbon.LBalancer;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description : 轮询的算法
 * @ClassName PollBalancer
 * @Author lb
 * @Date 2021/11/30 16:13
 * @Version 1.0
 */
public class PollBalancer implements LBalancer {

    /**
     * 当前拿到的index
     */
    private AtomicInteger index = new AtomicInteger(0);


    private int getIndex(List<ServiceInstance> services) {
        int concurrent, i;
        for (; ; ) {
            concurrent = index.get();
            i = ((concurrent + 1) % services.size());
            if (index.compareAndSet(concurrent, i)) {
                return i;
            }
        }
    }

    @Override
    public ServiceInstance choose(List<ServiceInstance> services) {
        return services.get(getIndex(services));
    }
}
