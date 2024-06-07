package net.risedata.rpc.consumer.result.value;


import net.risedata.rpc.consumer.result.SyncResult;
import net.risedata.rpc.factory.model.ReturnType;

/**
 * @description: 返回值处理器接口
 * @Author lb176
 * @Date 2021/8/4==14:53
 */
public interface ReturnValueHandle {
    /**
     * 是否为此处理器处理
     * @param returnType 返回值包装类
     * @return 是/否
     */
    boolean isHandle(ReturnType returnType);

    /**
     * 在result中拿到值
     *
     * @param result result
     * @return
     */
    Object getValue(SyncResult result, ReturnType returnType, Object[] args);
}
