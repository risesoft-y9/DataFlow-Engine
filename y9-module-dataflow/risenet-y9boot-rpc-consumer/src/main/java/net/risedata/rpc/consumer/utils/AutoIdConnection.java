package net.risedata.rpc.consumer.utils;

import net.risedata.rpc.model.Msg;

import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @description: 自动id生成 递增
 * @Author lb176
 * @Date 2021/8/4==10:27
 */
public abstract class AutoIdConnection extends SimpleChannelInboundHandler<Msg> {
    private long id;

    private static final long MAX = Long.MAX_VALUE - 1;

    protected synchronized long getId() {
        if (id > MAX) {
            id = 0;
        } ;
        return id++;
    }

}
