package net.risedata.rpc.provide.net;


import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.risedata.rpc.model.ListenerResponse;
import net.risedata.rpc.model.Msg;
import net.risedata.rpc.model.Request;
import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.config.Application;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.exceptions.ListenerException;
import net.risedata.rpc.provide.handle.impl.DispatcherHandle;
import net.risedata.rpc.provide.listener.Listener;
import net.risedata.rpc.provide.listener.SyncResult;
import net.risedata.rpc.provide.listener.impl.SyncListenerResult;
import net.risedata.rpc.provide.model.ListenerRequest;
import net.risedata.rpc.service.RPCExecutorService;
import net.risedata.rpc.utils.IdUtils;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * @description: server端的执行器用于执行方法以及返回
 * @Author lb176
 * @Date 2021/4/29==16:32
 */
public class ClinetConnection extends SimpleChannelInboundHandler<Msg> {

    /**
     * 实际用来执行任务的线程service
     */
    public static RPCExecutorService EXECUTOR_SERVICE;

    /**
     * 当前连接的id
     */
    private String id;
    /**
     * 当前连接的监听器
     */
    private Listener listener;


    public ClinetConnection(String id, Listener listener) {
        this.id = id;
        this.listener = listener;
        this.resultMap = new ConcurrentHashMap<>();
    }


    private Map<String, Object> attributes;

    /**
     * 通道
     */
    private ChannelHandlerContext channelHandlerContext;
    /**
     * 返回值存放的map
     */
    private Map<Long, SyncListenerResult> resultMap;

    public void pushListener(String name, Map<String, Object> args) {
        pushListener(name, args, false, 0);
    }

    public SyncResult pushListener(String name, Map<String, Object> args, long timeOut) {
        return pushListener(name, args, true, timeOut);
    }

    /**
     * 发送一个事件给该连接
     *
     * @param name     事件名
     * @param args     参数
     * @param isResult 是否等待返回值如果为false 则返回空
     * @param timeOut  等待返回值超时时间
     */
    public SyncResult pushListener(String name, Map<String, Object> args, boolean isResult, long timeOut) {
        try {
            if (channelHandlerContext.isRemoved()) {
                if (isResult) {
                    return SyncListenerResult.asError(new ListenerException("connection is removed"));
                }
                return null;
            }
            Response res = new Response();
            ListenerRequest listenerRequest = new ListenerRequest();
            listenerRequest.setId(IdUtils.getId());
            listenerRequest.setBack(isResult);
            listenerRequest.setMapping(name);
            listenerRequest.setArgs(args);
            res.setMessage(JSON.toJSONString(listenerRequest));
            res.setType(Request.LISTENER);
            channelHandlerContext.writeAndFlush(res.asMsg());
            if (isResult) {
                SyncListenerResult sync = new SyncListenerResult(listenerRequest);
                resultMap.put(listenerRequest.getId(), sync);
                if (timeOut > 0) {
                    channelHandlerContext.channel().eventLoop().schedule(() -> {
                        if (resultMap.remove(listenerRequest.getId()) != null) {
                            sync.error(new ListenerException("调用超时!"));
                        }
                    }, timeOut, TimeUnit.MILLISECONDS);
                }
                return sync;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (isResult) {
                return SyncListenerResult.asError(new ListenerException(e.getMessage()));
            }
            throw new ListenerException(e.getMessage());

        }

    }

    public void writeAndFlush(Msg asMsg) {
        channelHandlerContext.writeAndFlush(asMsg);
    }

    /**
     * 在此方法中改成异步
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Msg msg) throws Exception {
        Request request = Request.as(msg);
        //TODO 此处拿到了请求的id  TODO 调用内部自带的操作方法
        try {
            RPCRequestContext rpcRequestContext = new RPCRequestContext(this, request);
            switch (request.getType()) {
                case Request.RPC:
                    EXECUTOR_SERVICE.executor(() -> {
                        DispatcherHandle.handle(request, rpcRequestContext);
                    });
                    break;
                case Request.LISTENER:
                    EXECUTOR_SERVICE.executor(() -> {
                        SyncListenerResult result = resultMap.remove(request.getId());
                        if (result != null) {
                            ListenerResponse listenerResponse = request.getArgs().getObject(0, ListenerResponse.class);
                            if (listenerResponse.getStatus() == Response.ERROR) {
                                result.error(new ListenerException(listenerResponse.getMsg()));
                            } else {
                                result.success(listenerResponse.getMsg());
                            }
                        }
                    });

                    break;
                default:
                    rpcRequestContext.sendError("undefined type:" + request.getType());
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取连接的ip地址
     *
     * @return
     */
    public SocketAddress getRemoteAddress() {
        return channelHandlerContext.channel().remoteAddress();
    }

    /**
     * 关闭当前连接
     */
    public void close() {
        this.channelHandlerContext.close();
    }

    /**
     * 拿到当前连接的id
     *
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * 往该连接添加一个值
     *
     * @param key
     * @param value
     * @return
     */
    public Object setAttribute(String key, Object value) {
        initAttributes();
        return this.attributes.put(key, value);
    }


    private void initAttributes() {
        if (this.attributes == null) {
            synchronized (id) {
                if (this.attributes == null) {
                    this.attributes = new ConcurrentHashMap<>();
                }
            }
        }
    }

    /**
     * 往该连接里面获取一个值
     *
     * @param key
     * @return
     */
    public Object getAttribute(String key) {
        initAttributes();
        return this.attributes.get(key);
    }

    /**
     * 删除该连接
     *
     * @param key
     * @return
     */
    public Object removeAttribute(String key) {
        initAttributes();
        return this.attributes.remove(key);
    }

    /**
     * 此时才可以进行发生消息也就是onconnection
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.channelHandlerContext = ctx;
        listener.onConnection(this);
    }

    /**
     * oncolse的
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        listener.onClosed(this);
        Application.logger.info(ctx.channel().remoteAddress() + " closed :");
        //关闭所有正在调用的方法
        for (SyncListenerResult listenerResult : resultMap.values()) {
            listenerResult.error(new ListenerException("connection remove"));
        }
        resultMap.clear();

    }

    /**
     * 出现异常关闭连接
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        Application.logger.info(ctx.channel().remoteAddress() + " closed :" + cause.getMessage());
        cause.printStackTrace();

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Application.logger.info(ctx.channel().remoteAddress() + " connection");
    }


}
