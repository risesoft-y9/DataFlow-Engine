package net.risedata.rpc.consumer.core.impl;

import com.alibaba.fastjson.JSON;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import net.risedata.rpc.consumer.config.ConsumerApplication;
import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.core.ConnectionPool;
import net.risedata.rpc.consumer.exceptions.RpcException;
import net.risedata.rpc.consumer.exceptions.RpcTimeOutException;
import net.risedata.rpc.consumer.listener.ConnectionListener;
import net.risedata.rpc.consumer.listener.ListenerDispatch;
import net.risedata.rpc.consumer.model.ListenerRequest;
import net.risedata.rpc.consumer.model.PortAndHost;
import net.risedata.rpc.consumer.result.SyncParseResult;
import net.risedata.rpc.consumer.result.SyncResult;
import net.risedata.rpc.consumer.result.genericity.GenericitySyncResult;
import net.risedata.rpc.consumer.result.genericity.ListGenericitySyncResult;
import net.risedata.rpc.consumer.result.impl.DefaultSyncParseResult;
import net.risedata.rpc.consumer.utils.AutoIdConnection;
import net.risedata.rpc.model.ListenerResponse;
import net.risedata.rpc.model.Msg;
import net.risedata.rpc.model.Request;
import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.config.Application;
import net.risedata.rpc.utils.IdUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @description: 通道实现
 * @Author lb176
 * @Date 2021/8/4==10:38
 */
public class ChannelConnection extends AutoIdConnection implements Connection {
    /**
     * 上下文对象
     */
    private Channel ctx;

    private PortAndHost portAndHost;


    private ConnectionManager connectionManager;
    /**
     * 监听器
     */
    private ConnectionListener listener;
    /**
     * 是否已经被移除出去的 注意如果已经被移除出去了则 调用此对象的execution方法会转接到 pool中
     */
    private volatile boolean isRemove = true;
    private boolean use = true;
    /**
     * 返回值存放的map
     */
    private Map<Long, SyncParseResult> resultMap = new ConcurrentHashMap<>();

    /**
     * 连接池对象用于后续的熔断操作
     */
    private ConnectionPool connectionPool;

    public ChannelConnection(String host, int port, ConnectionPool connectionPool, ConnectionManager connectionManager) {
        this.portAndHost = new PortAndHost(host, port);
        this.connectionPool = connectionPool;
        this.connectionManager = connectionManager;
    }

    @Override
    public void setListener(ConnectionListener listener) {
        this.listener = listener;
    }


    @Override
    public PortAndHost getPortAndHost() {
        return portAndHost;
    }

    private SyncResult fusingExecution(String mapping, int timeOut, Object... args) {
        if (connectionPool.size() < 1) {
            throw new RpcException("After the message is sent, the connection is broken and no connections are available");
        }
        return ((Connection) connectionPool.getConnection()).executionSync(mapping, timeOut, args);
    }


    @Override
    public <T> T execution(String service, String api, Class<T> returnType, int timeOut, Object... args) {
        return executionSync(service, api, timeOut, args).getResult().getValue(returnType);
    }

    @Override
    public <T> List<T> executionList(String service, String api, Class<T> returnType, int timeOut, Object... args) {
        return executionSync(service, api, timeOut, args).getResult().getList(returnType);
    }

    @Override
    public SyncResult executionSync(String className, String methodName, int timeOut, Object... args) {
        return executionSync(className + "/" + methodName, timeOut, args);
    }

    @Override
    public <T> GenericitySyncResult<T> executionSyncAs(String service, String api, int timeOut, Class<T> type, Object... args) {
        return executionSync(service, api, timeOut, args).as(type);
    }

    @Override
    public <T> ListGenericitySyncResult<T> executionSyncAsList(String service, String api, int timeOut, Class<T> type, Object... args) {
        return executionSync(service, api, timeOut, args).asList(type);
    }

    @Override
    public boolean isRemoved() {
        return isRemove;
    }

    @Override
    public boolean isHasBeenLinked() {
        return ctx != null;
    }

    @Override
    public <T> T execution(String mapping, Class<T> returnType, int timeOut, Object... args) {
        return executionSync(mapping, timeOut, args).getResult().getValue(returnType);
    }

    @Override
    public <T> List<T> executionList(String mapping, Class<T> returnType, int timeOut, Object... args) {
        return executionSync(mapping, timeOut, args).getResult().getList(returnType);
    }

    @Override
    public SyncResult executionSync(String mapping, int timeOut, Object... args) {
        if (isRemove) {
            return fusingExecution(mapping, timeOut, args);
        }

        Request request = new Request(mapping, IdUtils.getId(), args);
        SyncParseResult result = new DefaultSyncParseResult(request);
        resultMap.put(request.getId(), result);
        ctx.writeAndFlush(request.asMsg());
        if (timeOut > 0) {
            ctx.eventLoop().schedule(() -> {
                if (resultMap.remove(request.getId()) != null) {
                    result.error(new RpcTimeOutException(request.getUrl() + "调用超时", request));
                }
            }, timeOut, TimeUnit.MILLISECONDS);
        }
        return result;
    }

    @Override
    public <T> GenericitySyncResult<T> executionSyncAs(String mapping, int timeOut, Class<T> type, Object... args) {
        return executionSync(mapping, timeOut, args).as(type);
    }

    @Override
    public <T> ListGenericitySyncResult<T> executionSyncAsList(String mapping, int timeOut, Class<T> type, Object... args) {
        return executionSync(mapping, timeOut, args).asList(type);
    }


    @Override
    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    @Override
    public int concurrentActionSize() {
        return resultMap.size();
    }

    @Override
    public boolean isUse() {
        return use;
    }

    @Override
    public void abandon() {
        this.use = false;
        listener.onClose(this);
        this.isRemove = true;

    }

    @Override
    public boolean enable() {
        if (ctx.isActive()) {
            this.use = true;
            this.isRemove = false;
            listener.onConnection(this);
            return true;
        }
        return false;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        Response response = Response.as(msg);
        switch (response.getType()) {
            case Request.RPC:
                SyncParseResult result = resultMap.remove(response.getId());
                if (result != null) {
                    if (response.getStatus() == Response.ERROR) {
                        result.error(new RpcException(response.getMessage()));
                    } else {
                        result.success(response.getMessage());
                    }
                }
                break;
            case Request.LISTENER:
                ConsumerApplication.EXECUTOR_SERVICE.executor(() -> {
                    ListenerRequest request = JSON.parseObject(response.getMessage(), ListenerRequest.class);
                    Request req = null;
                    ListenerResponse response1 = null;
                    try {
                        Object res = ListenerDispatch.doDispatch(request);

                        if (request.isBack()) {
                            response1 = new ListenerResponse();
                            response1.setStatus(Response.SUCCESS);
                            response1.setMsg(JSON.toJSONString(res));
                            req = new Request("", request.getId(), response1);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (request.isBack()) {
                            response1 = new ListenerResponse();
                            response1.setStatus(Response.ERROR);
                            response1.setMsg(e.getMessage());
                            req = new Request("", request.getId(), response1);
                        }
                    }
                    if (req != null) {

                        req.setType(Request.LISTENER);
                        if (ctx.channel().isActive() && !isRemove) {
                            ctx.writeAndFlush(req.asMsg());
                        } else {
                            ListenerDispatch.backError(request, response1, this);
                        }

                    }
                });

                break;
            default:
                throw new RpcException("未知的类型" + response);
        }


    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Application.logger.info(portAndHost + " connection ");

        this.ctx = ctx.channel();
        isRemove = false;
        listener.onConnection(this);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }


    private void listenerClose() {

        isRemove = true;
        resultMap.forEach((k, v) -> {
            v.error(new RpcException("close ed"));
        });
        listener.onClose(this);

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        listenerClose();

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void close() {
        ctx.close();
    }

    @Override
    public String toString() {
        return "ChannelConnection{" +
                "portAndHost=" + portAndHost +
                ", isRemove=" + isRemove +
                '}';
    }
}
