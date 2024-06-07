package net.risedata.register.model;

import java.util.List;
import java.util.Map;

import net.risedata.register.service.IServiceInstance;

/**
 * @Description : 服务端的api接口
 * @ClassName RegisterServerAPI
 * @Author lb
 * @Date 2021/11/26 9:47
 * @Version 1.0
 */
public interface RegisterServerAPI {

    public static final String GET_ALL="register/getServices";

    /**
     * 拿到当前的所有服务信息
     *
     * @return
     */
    Object getServiceName();


    /**
     * 根据名字获取实例
     *
     * @return
     */
    List<IServiceInstance> getServiceByName(String name);


    /**
     * 获取所有实例
     *
     * @return
     */
    Map<String, List<IServiceInstance>> getServices(String environment);

    /**
     * 注册服务到中心
     *
     * @param serviceInstance 实例
     * @return 返回当前服务器是否存在信息
     */

    boolean register( IServiceInstance serviceInstance);

    /**
     * 删除一个服务
     *
     * @param name       服务名
     * @param instanceId 实例id
     * @return
     */
    boolean remove(String name, String instanceId,Long time);

}
