package net.risedata.rpc.provide.context;


import com.alibaba.fastjson.JSON;

import io.netty.channel.ChannelHandlerContext;
import net.risedata.rpc.model.Request;
import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.defined.MethodDefined;
import net.risedata.rpc.provide.exceptions.SenderException;
import net.risedata.rpc.provide.filter.Filter;
import net.risedata.rpc.provide.filter.FilterNode;
import net.risedata.rpc.provide.filter.impl.FilterNodeApplication;
import net.risedata.rpc.provide.net.ClinetConnection;

/**
 * @description: rpc 调用的上下文
 * @Author lb176
 * @Date 2021/9/8==15:29
 */
public class RPCRequestContext {
    /**
     * 当前的连接
     */
    private ClinetConnection concurrentConnection;
    /**
     * request
     */
    private Request request;
    /**
     * 异步发送时调用的方法
     */
    private SendFunction sendFunction;
    /**
     * 方法定义
     */
    private MethodDefined methodDefined;
    /**
     * 是否已经发送
     */
    private boolean isSend = false;
    /**
     * 相关filter
     */
    private FilterNodeApplication filterApplication;


    public RPCRequestContext(ClinetConnection concurrentConnection, Request request) {
        this.request = request;
        this.concurrentConnection = concurrentConnection;
    }


    public FilterNodeApplication getFilterApplication() {
        return filterApplication;
    }

    public void setFilterApplication(FilterNodeApplication filterApplication) {
        this.filterApplication = filterApplication;
    }


    public MethodDefined getMethodDefined() {
        return methodDefined;
    }

    public void setMethodDefined(MethodDefined methodDefined) {
        this.methodDefined = methodDefined;
    }

    /**
     * 获取当前的连接对象
     * @return
     */
    public ClinetConnection getConcurrentConnection() {
        return concurrentConnection;
    }


    public Request getRequest() {
        return request;
    }

    /**
     * 设置调用send的时候触发的方法(由框架自定义操作)
     *
     * @param sendFunction
     */
    public void setSendRunable(SendFunction sendFunction) {

        this.sendFunction = sendFunction;
    }

    /**
     * 发送一个异常
     * @param msg
     */
    public void sendError(Object msg){
        send(new Response(Response.ERROR, JSON.toJSONString(msg), request.getId()));
    }


    /**
     * 发送一个成功的返回值注意必须是json
     * @param msg
     */
    public void sendSuccess(Object msg){
       send(new Response(Response.SUCCESS, JSON.toJSONString(msg), request.getId()));
    }

    /**
     * 将消息发送给当前通道
     *
     * @param response
     */
    public void send(Response response) {
        if (this.isSend == true){
          throw new SenderException("request send");
        }
        this.isSend = true;
        if (sendFunction == null) {
            concurrentConnection.writeAndFlush(response.asMsg());
        } else {
            try {
                sendFunction.send( response, (res) -> {
                    concurrentConnection.writeAndFlush(res.asMsg());
                });
            }catch (Exception e){
                e.printStackTrace();
                throw new SenderException(e.getMessage());
            }
        }
    }
}

