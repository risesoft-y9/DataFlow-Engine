package net.risedata.rpc.provide.handle;

import net.risedata.rpc.provide.defined.ParameterDefined;
import net.risedata.rpc.utils.LParameter;


/**
 * @description: 处理参数
 * @Author lb176
 * @Date 2021/4/29==16:31
 */
public interface ParameterHandle {


    /**
     * 是否由此处理器处理
     *
     * @param p 参数
     * @return 返回是则由此处理
     */
    boolean isHandle(LParameter p);

    /**
     * 由此处理器处理则返回处理器
     *
     * @param p 参数
     * @return 返回对应的参数处理器这个处理器将和参数绑定在一起
     */
    ParameterDefined getParameterDefined(LParameter p);
}
