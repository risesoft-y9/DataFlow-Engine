package net.risedata.rpc.consumer.task;

import net.risedata.rpc.Task.Task;
import net.risedata.rpc.consumer.core.impl.CloudConnectionManager;
import net.risedata.rpc.provide.config.Application;

/**
 * @description: 用于于springCloud 整合的重连task类
 * @Author lb176
 * @Date 2021/4/29==15:17
 */
public class CloudTask implements Task {
    private CloudConnectionManager cloudConnectionManager;
    private String serviceName;


    public CloudTask(CloudConnectionManager cloudConnectionManager, String serviceName) {
        this.cloudConnectionManager = cloudConnectionManager;
        this.serviceName = serviceName;

    }


    @Override
    public void run() {
        cloudConnectionManager.connection(serviceName);
    }
}
