package net.risesoft.api.message.impl;

import net.risesoft.api.message.InstanceMessage;
import net.risesoft.api.persistence.model.IServiceInstanceModel;

/**
 * @Description : 默认的instance 消息创建器
 * @ClassName DefaultInstanceMessage
 * @Author lb
 * @Date 2022/8/30 15:05
 * @Version 1.0
 */
public class DefaultInstanceMessage implements InstanceMessage {
    @Override
    public String createContent(IServiceInstanceModel serviceInstanceModel) {
        StringBuilder content = new StringBuilder("【下线警告】:");
        content.append("服务名:").append(serviceInstanceModel.getServiceId()).append(":")
                .append(serviceInstanceModel.getDescription()).append("--ip地址:").append(serviceInstanceModel.getHost())
                .append("--端口:").append(serviceInstanceModel.getPort());
        return content.toString();
    }

    @Override
    public String createTitle(IServiceInstanceModel serviceInstanceModel) {
        return "下线通知:";
    }
}
