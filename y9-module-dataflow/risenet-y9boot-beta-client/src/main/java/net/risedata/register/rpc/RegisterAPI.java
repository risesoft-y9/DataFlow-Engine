package net.risedata.register.rpc;

import net.risedata.register.service.IServiceInstance;
import net.risedata.rpc.consumer.annotation.API;
import net.risedata.rpc.consumer.annotation.RPCClient;
import net.risedata.rpc.consumer.result.SyncResult;

import java.util.List;

/**
 * @Description : 服务注册调用Rpc的api
 * @ClassName RegisterAPIService
 * @Author lb
 * @Date 2021/11/26 11:17
 * @Version 1.0
 */
@RPCClient(value = {"${beta.servers}"}, name = "register", managerName = RegisterAPI.MANAGER_NAME)
public interface RegisterAPI {

    public static final String MANAGER_NAME = "register_rpc_manager";


    /**
     * 拿到当前的所有服务信息
     *
     * @return
     */
    @API
    Object getServiceName();


    /**
     * 根据名字获取实例
     *
     * @return
     */
    @API
    List<IServiceInstance> getServiceByName(String name);


    /**
     * 获取所有实例
     *
     * @return
     */
    @API
    SyncResult getServices(String environment);

    /**
     * 注册服务到中心
     *
     * @param serviceInstance 实例
     * @return 返回当前服务器存在的所有信息
     */
    @API
    SyncResult register(IServiceInstance serviceInstance);

    /**
     * 删除一个服务
     *
     * @param name       服务名
     * @param instanceId 实例id
     * @return
     */
    @API
    boolean remove(String name, String instanceId);


    /**
     * 刷新时间
     *
     * @return
     */
    @API
    SyncResult reNew();
}
