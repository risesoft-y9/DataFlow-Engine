package net.risedata.rpc.provide.filter;

import net.risedata.rpc.model.Response;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.context.Send;

/**
 * @Description : 过滤器
 * @ClassName Filter
 * @Author lb
 * @Date 2021/11/22 10:58
 * @Version 1.0
 */
public interface Filter {
     /**
      * 在进入请求之前
      * @param context 请求数据
      * @param filterContext 过滤器山下文
      */
     void doBefore(RPCRequestContext context,FilterContext filterContext)  throws Exception;

     /**
      * 在执行完请求之后
      * @param response
      * @param context
      * @param filterContext
      */
     void doAfter( Response response, RPCRequestContext context,FilterContext filterContext, Send send) throws Exception;

     /**
      * 获取优先级 优先级越小越靠前
      * @return
      */
    int getOrder();
}
