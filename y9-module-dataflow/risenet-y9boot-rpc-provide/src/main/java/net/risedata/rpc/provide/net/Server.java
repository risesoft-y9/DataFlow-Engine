package net.risedata.rpc.provide.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import net.risedata.rpc.coder.MsgDecoder;
import net.risedata.rpc.coder.MsgEncoder;
import net.risedata.rpc.provide.config.Application;
import net.risedata.rpc.provide.exceptions.ServerStartException;
import net.risedata.rpc.provide.listener.ListenerManager;
import net.risedata.rpc.provide.listener.impl.DefaultListenerManager;

/**
 * @description: 服务器端主要用于接收服务并且启动
 * @Author lb176
 * @Date 2021/4/29==16:32
 */
public class Server {

    private ServerBootstrap serverBootstrap;
    private NioEventLoopGroup root;
    private NioEventLoopGroup work;
    private ListenerManager listenerManager;
    private int port;

    public void close() {
        work.shutdownGracefully();
        root.shutdownGracefully();
    }

    /**
     * 创建服务器
     *
     * @param port     端口
     * @param workSize 工作线程的数量   默认 当前计算机的核数*2
     */
    public Server(int port, Integer workSize) {
        this.port = port;
        serverBootstrap = new ServerBootstrap();
        root = new NioEventLoopGroup(1);
        work = new NioEventLoopGroup(workSize == null ? 0 : workSize);

        listenerManager = new DefaultListenerManager();

        serverBootstrap.group(root, work)
                .channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ClientConnection clinetConnection = new ClientConnection(ch.id().asLongText(), listenerManager);
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new MsgDecoder());
                pipeline.addLast(new MsgEncoder());
                pipeline.addLast(clinetConnection);
            }
        });

    }

    public void start() {
        ChannelFuture sync = null;
        try {
            sync = serverBootstrap.bind(port).sync();
            sync.addListener((ch) -> {
                if (ch.isSuccess()) {
                    Application.logger.info("server:" + port + " start");
                } else {
                    throw new ServerStartException("server start fail" + ch.cause().getMessage());
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();
            work.shutdownGracefully();
            root.shutdownGracefully();
        }
    }

    public ListenerManager getlistenerManager() {
        return listenerManager;
    }
}
