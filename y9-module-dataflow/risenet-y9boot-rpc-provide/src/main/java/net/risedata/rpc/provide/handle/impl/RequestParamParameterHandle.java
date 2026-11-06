package net.risedata.rpc.provide.handle.impl;

import org.springframework.web.bind.annotation.RequestParam;

import net.risedata.rpc.provide.defined.ParameterDefined;
import net.risedata.rpc.provide.defined.impl.DefaultParameterDefined;
import net.risedata.rpc.provide.handle.ParameterHandle;
import net.risedata.rpc.utils.LParameter;

/**
 * 用来处理自定义注解@Param
 */
public class RequestParamParameterHandle implements ParameterHandle {
    @Override
    public boolean isHandle(LParameter p) {
        return p.getAnnotation(RequestParam.class) != null;
    }

    @Override
    public ParameterDefined getParameterDefined(LParameter p) {
        RequestParam param = p.getAnnotation(RequestParam.class);
        return new DefaultParameterDefined(param.required(), param.defaultValue(), p.getParameterType(),
            p.getParameterName(), p);
    }
}
