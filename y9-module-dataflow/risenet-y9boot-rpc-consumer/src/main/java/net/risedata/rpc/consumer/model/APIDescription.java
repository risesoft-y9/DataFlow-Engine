package net.risedata.rpc.consumer.model;

import net.risedata.rpc.consumer.invoke.InvokeHandle;
import net.risedata.rpc.consumer.result.value.ReturnValueHandle;
import net.risedata.rpc.factory.model.ReturnType;

/**
 * @description: api的描述信息包装类
 * @Author lb176
 * @Date 2021/4/29==15:16
 */
public class APIDescription {
    /**
     * 超时时间
     */
    private int timeOut;
    /**
     * serviceName+"/"+apiName
     */
    private String mapping;
    /**
     * 是否为sync的操作
     */
    private boolean isSync;
    /**
     * 返回值配置
     */
    private ReturnType returnType;
    /**
     * 处理此方法返回值的处理器
     */
    private ReturnValueHandle returnValueHandle;
    /**
     * 方法执行器
     */
    private InvokeHandle invokeHandle;

    /**
     * 拿到改方法执行的方法执行器
     *
     * @return 方法执行器
     */
    public InvokeHandle getInvokeHandle() {
        return invokeHandle;
    }

    public void setInvokeHandle(InvokeHandle invokeHandle) {
        this.invokeHandle = invokeHandle;
    }

    public ReturnValueHandle getReturnValueHandle() {
        return returnValueHandle;
    }

    public void setReturnValueHandle(ReturnValueHandle returnValueHandle) {
        this.returnValueHandle = returnValueHandle;
    }

    public APIDescription(int timeOut, String mapping) {
        this.timeOut = timeOut;
        this.mapping = mapping;
    }

    public ReturnType getReturnType() {
        return returnType;
    }

    public void setReturnType(ReturnType returnType) {
        this.returnType = returnType;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setSync(boolean sync) {
        isSync = sync;
    }

    /**
     * 拿到超时时间
     *
     * @return 超时时间
     */
    public int getTimeOut() {
        return timeOut;
    }

    /**
     * 拿到映射
     * @return
     */
    public String getMapping() {
        return mapping;
    }

    @Override
    public String toString() {
        return "APIDescription{" +
                "timeOut=" + timeOut +
                ", mapping='" + mapping + '\'' +
                ", isSync=" + isSync +
                ", returnType=" + returnType +
                ", returnValueHandle=" + returnValueHandle +
                ", invokeHandle=" + invokeHandle +
                '}';
    }
}
