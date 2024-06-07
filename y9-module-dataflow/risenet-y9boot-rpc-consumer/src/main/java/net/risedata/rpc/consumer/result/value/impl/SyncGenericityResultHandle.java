package net.risedata.rpc.consumer.result.value.impl;

import net.risedata.rpc.consumer.result.SyncResult;
import net.risedata.rpc.consumer.result.genericity.GenericitySyncResult;
import net.risedata.rpc.consumer.result.value.ReturnValueHandle;
import net.risedata.rpc.factory.model.ReturnType;

/**
 * @description: 处理异步的泛型
 * @Author lb176
 * @Date 2021/8/4==15:14
 */
public class SyncGenericityResultHandle implements ReturnValueHandle {
    @Override
    public boolean isHandle(ReturnType returnType) {
        return returnType.getReturnType() == GenericitySyncResult.class;
    }

    @Override
    public Object getValue(SyncResult result, ReturnType returnType, Object[] args) {
        return result.as(returnType.toGenericityClass(args));
    }


}
