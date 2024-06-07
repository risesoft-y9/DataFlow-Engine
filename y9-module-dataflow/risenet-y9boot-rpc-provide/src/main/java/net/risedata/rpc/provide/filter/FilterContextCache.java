package net.risedata.rpc.provide.filter;

import net.risedata.rpc.provide.defined.MethodDefined;
import net.risedata.rpc.provide.filter.impl.FilterNodeApplication;

/**
 * @Description : 过滤器上下文缓存
 * @ClassName FilterContextCache
 * @Author lb
 * @Date 2021/11/23 9:31
 * @Version 1.0
 */
public class FilterContextCache {
    /**
     * 过滤器
     */
    FilterNodeApplication filters;
    /**
     * 方法定义
     */
    MethodDefined methodDefined;


    public FilterNodeApplication getFilters() {
        return filters;
    }

    public void setFilters(FilterNodeApplication filters) {
        this.filters = filters;
    }

    public MethodDefined getMethodDefined() {
        return methodDefined;
    }

    public void setMethodDefined(MethodDefined methodDefined) {
        this.methodDefined = methodDefined;
    }

}
