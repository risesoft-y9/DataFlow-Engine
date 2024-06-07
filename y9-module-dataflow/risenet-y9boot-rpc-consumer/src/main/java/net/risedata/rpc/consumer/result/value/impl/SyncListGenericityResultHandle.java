package net.risedata.rpc.consumer.result.value.impl;

import net.risedata.rpc.consumer.result.SyncResult;
import net.risedata.rpc.consumer.result.genericity.GenericitySyncResult;
import net.risedata.rpc.consumer.result.genericity.ListGenericitySyncResult;
import net.risedata.rpc.consumer.result.value.ReturnValueHandle;
import net.risedata.rpc.factory.model.ReturnType;

/**
 * @description: 处理异步的list 泛型
 * @Author lb176
 * @Date 2021/8/4==15:14
 */
public class SyncListGenericityResultHandle implements ReturnValueHandle {
    @Override
    public boolean isHandle(ReturnType returnType) {

        return returnType.getReturnType() == ListGenericitySyncResult.class;
    }

    @Override
    public Object getValue(SyncResult result, ReturnType returnType, Object[] args) {
        return result.asList(returnType.toGenericityClass(args));
    }


}
