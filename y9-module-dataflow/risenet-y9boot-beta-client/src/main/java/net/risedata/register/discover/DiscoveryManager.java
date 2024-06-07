package net.risedata.register.discover;

import net.risedata.register.service.IServiceInstance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description : 管理服务
 * @ClassName DiscoveryManager
 * @Author lb
 * @Date 2021/11/26 10:01
 * @Version 1.0
 */
public class DiscoveryManager {


    private static final Map<String, List<ServiceInstance>> SERVICES = new ConcurrentHashMap<>();
    public static final Logger LOG = LoggerFactory.getLogger(DiscoveryManager.class);


    private static List<String> instanceNames = new ArrayList<>();

    /**
     * 注册一个实例进入到管理中心
     *
     * @param name     实例名
     * @param instance 实例数据
     */
    public synchronized static boolean register(String name, IServiceInstance instance) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("register ：" + name + " instances " + instance);
        }
        List<ServiceInstance> instances = SERVICES.get(name);
        if (instances == null) {
            instances = new ArrayList<>();
            SERVICES.put(name, instances);
        }
        //更新时间做比较
        for (ServiceInstance serviceInstance : instances) {
            if (serviceInstance.getInstanceId().equals(instance.getInstanceId())) {
                if (!((IServiceInstanceImpl) serviceInstance).getIServiceInstance().toString().equals(instance.toString()) && ((IServiceInstanceImpl) serviceInstance).getIServiceInstance().getUpdateTime() < instance.getUpdateTime()) {
                    ((IServiceInstanceImpl) serviceInstance).setIServiceInstance(instance);
                }
                return false;
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(instance.getInstanceId() + ": 已上线");
        }
        instances.add(new IServiceInstanceImpl(instance));
        if (!instanceNames.contains(name)) {
            instanceNames.add(name);
        }

        return true;
    }

    /**
     * 根据实例id获取实例
     *
     * @param key 实例key
     * @return
     */
    public static List<ServiceInstance> getService(String key) {
        return SERVICES.get(key);
    }

    /**
     * 获取所有实例的名字
     *
     * @return
     */
    public static List<String> getServices() {
        return new ArrayList<>(SERVICES.keySet());

    }

    /**
     * 拿到当前已经注册的所有服务
     *
     * @return
     */
    public synchronized static Map<String, List<ServiceInstance>> getAll() {
        return SERVICES;
    }

    /**
     * 根据实例id删除一个实例
     *
     * @param name 根据实例id 删除整个服务
     * @return
     */
    public synchronized static boolean remove(Object name) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("remove ：" + name + " instances " + name);
        }
        return SERVICES.remove(name) != null;
    }

    /**
     * 根据实例id删除一个实例
     *
     * @param id 根据实例id和实例名 删除一个实例
     * @return
     */
    public synchronized static boolean remove(Object name, Object id) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("remove ：" + name + " instances " + id);
        }

        List<ServiceInstance> instances = SERVICES.get(name);
        if (instances != null) {
            synchronized (instances) {
                for (int i = 0; i < instances.size(); i++) {
                    if (instances.get(i).getInstanceId().equals(id)) {
                        instances.remove(i);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(name + ":" + id + ": 已下线");
                        }
                        if (instances.size() == 0) {
                            instanceNames.remove(name);
                            SERVICES.remove(name);
                        }
                       return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 更新当前所有的服务
     *
     * @param name              服务名
     * @param iServiceInstances 实例
     */
    public synchronized static void update(String name, List<IServiceInstance> iServiceInstances) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("update ：" + name + " instances " + iServiceInstances);
        }

        List<ServiceInstance> instances = new ArrayList<>(iServiceInstances.size());
        for (IServiceInstance iServiceInstance : iServiceInstances) {
            instances.add(new IServiceInstanceImpl(iServiceInstance));
        }
        SERVICES.put(name, instances);
    }


}
