package net.risedata.register.rpc;

import net.risedata.register.service.IServiceInstance;
import net.risedata.rpc.consumer.annotation.API;
import net.risedata.rpc.consumer.annotation.RPCClinet;
import net.risedata.rpc.consumer.result.SyncResult;

import java.util.List;

/**
 * @Description : 服务注册调用Rpc的api
 * @ClassName RegisterAPIService
 * @Author lb
 * @Date 2021/11/26 11:17
 * @Version 1.0
 */
@RPCClinet( name = "job", managerName = RegisterAPI.MANAGER_NAME)
public interface JobAPI {
    /**
     * 刷新时间
     *
     * @return
     */
    @API
    SyncResult endJob(Integer jobId,String jobLogId,String result,String msg,Integer status);
}
