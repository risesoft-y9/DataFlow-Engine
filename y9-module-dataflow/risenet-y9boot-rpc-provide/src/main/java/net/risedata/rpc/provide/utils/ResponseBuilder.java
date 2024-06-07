package net.risedata.rpc.provide.utils;

import com.alibaba.fastjson.JSON;

import net.risedata.rpc.model.Request;
import net.risedata.rpc.model.Response;

/**
 * @Description : 用于创建response
 * @ClassName ResponseBuilder
 * @Author lb
 * @Date 2021/11/22 11:13
 * @Version 1.0
 */
public class ResponseBuilder {
    /**
     * 创建一个异常的返回值
     * @param msg 异常信息
     * @param request request
     * @return
     */
    public static Response createError(Object msg, Request request){
      Response res = new Response(Response.ERROR, JSON.toJSONString(msg),request.getId());
      return res;
    }

    /**
     * 创建一个成功的回调
     * @param msg 数据
     * @param request request
     * @return
     */
    public static Response createSuccess(Object msg, Request request){
        Response res = new Response(Response.SUCCESS, JSON.toJSONString(msg),request.getId());
        return res;
    }

}
