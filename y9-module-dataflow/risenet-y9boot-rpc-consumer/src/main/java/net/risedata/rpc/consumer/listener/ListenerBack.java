package net.risedata.rpc.consumer.listener;

import net.risedata.rpc.consumer.core.impl.ChannelConnection;
import net.risedata.rpc.consumer.model.ListenerRequest;
import net.risedata.rpc.model.ListenerResponse;
import net.risedata.rpc.model.Request;

/**
 * @Description : 监听回调异常连接被断开
 * @ClassName ListenerBack
 * @Author lb
 * @Date 2023/1/29 9:44
 * @Version 1.0
 */
public interface ListenerBack {

    void onBackError(ListenerRequest request , ListenerResponse response, ChannelConnection channelConnection);
}
