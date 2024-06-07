package net.risedata.rpc.provide.handle.impl;

import net.risedata.rpc.provide.defined.ParameterDefined;
import net.risedata.rpc.provide.defined.impl.DefaultParameterDefined;
import net.risedata.rpc.provide.handle.ParameterHandle;
import net.risedata.rpc.utils.LParameter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;


/**
 * 用来处理默认的这个handle负责最底下的收尾工作
 */
public class DefaultParameterHandle  implements ParameterHandle {
    @Override
    public boolean isHandle(LParameter p) {
        return true;
    }

    @Override
    public ParameterDefined getParameterDefined(LParameter p) {
        return new DefaultParameterDefined(false, null, p.getParameterType(),p.getParameterName(),p);
    }
}
