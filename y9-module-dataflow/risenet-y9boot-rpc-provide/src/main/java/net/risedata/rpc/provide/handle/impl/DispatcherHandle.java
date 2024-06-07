package net.risedata.rpc.provide.handle.impl;


import net.risedata.rpc.model.Request;
import net.risedata.rpc.provide.context.RPCRequestContext;
import net.risedata.rpc.provide.filter.FilterContext;
import net.risedata.rpc.provide.filter.FilterContextCache;
import net.risedata.rpc.provide.filter.FilterManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * 此执行器用来执行具体的方法
 */
public class DispatcherHandle {
    private static final Logger LOGGER = LoggerFactory.getLogger(DispatcherHandle.class);


    public static void handle(Request request, RPCRequestContext requestContext) {
        try {
            String url = request.getUrl();
            FilterContextCache cache = FilterManager.createFilterContext(url, request);
            if (cache != null) {
                requestContext.setMethodDefined(cache.getMethodDefined());
                requestContext.setFilterApplication(cache.getFilters());
            }
            FilterContext.doFilter(requestContext);
        } catch (Exception e) {
            if (e instanceof InvocationTargetException) {
                InvocationTargetException e2 = (InvocationTargetException) e;
                requestContext.sendError("error:" + e2.getTargetException().getMessage());
                LOGGER.error("error" + e2.getTargetException().getMessage());
            } else {
                e.printStackTrace();
                requestContext.sendError("error:" + e.getMessage());
                LOGGER.error("error" + e.getMessage());
            }

            e.printStackTrace();
        }

    }


}
