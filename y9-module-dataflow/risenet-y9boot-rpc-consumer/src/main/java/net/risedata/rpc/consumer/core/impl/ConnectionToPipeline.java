package net.risedata.rpc.consumer.core.impl;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.risedata.rpc.model.Msg;
import net.risedata.rpc.utils.KeyLock;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description :
 * @ClassName ConnectionToPipeline
 * @Author lb
 * @Date 2021/12/7 10:39
 * @Version 1.0
 */
@ChannelHandler.Sharable
public class ConnectionToPipeline extends SimpleChannelInboundHandler<Msg> {

    private static Map<String, ChannelConnection> connectionMap;
    public static  KeyLock<String> KEY_LOCK ;

    public ConnectionToPipeline(){
        KEY_LOCK = new KeyLock<>();
        connectionMap = new ConcurrentHashMap<>();
    }


    public void put(ChannelConnection channelConnection) {
        String urlId = channelConnection.getPortAndHost().getHost() + ":" + channelConnection.getPortAndHost().getPort();
        KEY_LOCK.lock(urlId);
        connectionMap.put(urlId, channelConnection);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        ctx.pipeline().remove(this);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        ;


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String id = socketAddress.getHostString() + ":" + socketAddress.getPort();
        ChannelConnection channelConnection = connectionMap.remove(id);
        ctx.pipeline().remove(this);
        ctx.pipeline().addLast(channelConnection);
        KEY_LOCK.unLock(id);
        try {
            channelConnection.channelActive(ctx);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
