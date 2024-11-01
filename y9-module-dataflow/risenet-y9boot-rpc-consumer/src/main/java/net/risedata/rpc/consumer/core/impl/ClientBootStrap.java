package net.risedata.rpc.consumer.core.impl;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import net.risedata.rpc.coder.MsgDecoder;
import net.risedata.rpc.coder.MsgEncoder;
import net.risedata.rpc.consumer.core.HostAndPortConnection;

/**
 * 客户端的 启动器一般只启动一个
 */
public class ClientBootStrap {
    Bootstrap bootstrap;
    private NioEventLoopGroup group;

    private static ConnectionToPipeline connectionToPipeline;

    public void close() {
        group.shutdownGracefully();


    }

    public ClientBootStrap() {
        this.bootstrap = new Bootstrap();

    }


    public void start(int threadSize) {
        connectionToPipeline = new ConnectionToPipeline();
        group = new NioEventLoopGroup(threadSize);
        this.bootstrap.group(group)
                .channel(NioSocketChannel.class);
        this.bootstrap.handler(
                new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new MsgDecoder());
                        pipeline.addLast(new MsgEncoder());
                        pipeline.addLast(connectionToPipeline);
                    }
                }
        );
    }

    public synchronized ChannelFuture connection(String host, int port, HostAndPortConnection connection) throws InterruptedException {

        connectionToPipeline.put((ChannelConnection) connection);
        return this.bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (!channelFuture.isSuccess()) {
                    ConnectionToPipeline.KEY_LOCK.unLock(host + ":" + port);
                }

            }
        });

    }

}
