package net.risedata.rpc.consumer.result.value.impl;

import net.risedata.rpc.consumer.result.Result;
import net.risedata.rpc.consumer.result.SyncResult;
import net.risedata.rpc.consumer.result.value.ReturnValueHandle;
import net.risedata.rpc.factory.model.ReturnType;

import java.util.List;
import java.util.Map;

/**
 * @description: 处理同步的list 类型
 * @Author lb176
 * @Date 2021/8/4==15:14
 */
public class ListHandle implements ReturnValueHandle {
    @Override
    public boolean isHandle(ReturnType returnType) {
        return returnType.isReturnTypeClass() &&
                List.class.isAssignableFrom(returnType.getReturnType());
    }

    @Override
    public Object getValue(SyncResult result, ReturnType returnType, Object[] args) {
        return returnType.isGenericity()
                ? result.getResult().getList(returnType.toGenericityClass(args))
                : result.getResult().getList(Map.class);
    }


}
