package net.risedata.rpc.consumer.result.value.impl;

import net.risedata.rpc.consumer.result.SyncResult;
import net.risedata.rpc.consumer.result.value.ReturnValueHandle;
import net.risedata.rpc.factory.model.ReturnType;

import java.util.List;
import java.util.Map;

/**
 * @description: 处理异步返回值
 * @Author lb176
 * @Date 2021/8/4==15:14
 */
public class SyncResultHandle implements ReturnValueHandle {
    @Override
    public boolean isHandle(ReturnType returnType) {
        return SyncResult.class.isAssignableFrom(returnType.getReturnType());
    }

    @Override
    public Object getValue(SyncResult result, ReturnType returnType, Object[] args) {
        return result;
    }


}
