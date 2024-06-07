package net.risedata.register.ribbon.impl;

import net.risedata.register.discover.IServiceInstanceImpl;
import net.risedata.register.ribbon.LBalancer;
import net.risedata.register.service.IServiceInstance;

import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

/**
 * @Description : 权重算法
 * @ClassName WeightBalancer
 * @Author lb
 * @Date 2021/11/30 16:13
 * @Version 1.0
 */
public abstract class AbstractWeightBalancer implements LBalancer {

    /**
     * 当前拿到的index
     */
    private int index = 0;

    abstract int getIndex(int maxIndex);

    @Override
    public ServiceInstance choose(List<ServiceInstance> services) {
        if (services == null || services.size() == 0) {
            return null;
        }
        int index = 0;
        IServiceInstance service1;

        for (ServiceInstance service : services) {
            service1 = ((IServiceInstanceImpl) service).getIServiceInstance();
            index += (service1.getWeight() + 1);

        }
        index = getIndex(index);
        for (ServiceInstance service : services) {
            service1 = ((IServiceInstanceImpl) service).getIServiceInstance();
            index -= (service1.getWeight() + 1);
            if (index < 0) {
                return service;
            }
        }
        return null;
    }
}
