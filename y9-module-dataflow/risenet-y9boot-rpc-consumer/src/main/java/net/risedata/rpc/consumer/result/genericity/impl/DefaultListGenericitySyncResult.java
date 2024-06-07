package net.risedata.rpc.consumer.result.genericity.impl;

import com.alibaba.fastjson.JSON;

import net.risedata.rpc.consumer.result.Error;
import net.risedata.rpc.consumer.result.genericity.ListGenericitySuccess;
import net.risedata.rpc.consumer.result.genericity.ListGenericitySyncResult;
import net.risedata.rpc.consumer.result.impl.DefaultSyncParseResult;

import java.util.List;

/**
 * @description: 默认的异步操作result类
 * @Author lb176
 * @Date 2021/8/4==11:45
 */
public class DefaultListGenericitySyncResult<T> implements ListGenericitySyncResult<T> {

    private DefaultSyncParseResult parseResult;


    private Class<T> returnType;

    public DefaultListGenericitySyncResult(DefaultSyncParseResult parseResult, Class<T> returnType) {
        this.parseResult = parseResult;
        this.returnType = returnType;
    }

    @Override
    public DefaultListGenericitySyncResult onSuccess(ListGenericitySuccess<T> success) {
        parseResult.onSuccess((res) -> {
            success.success(res.getList(returnType));
        });
        return this;
    }

    @Override
    public DefaultListGenericitySyncResult onError(Error error) {
        parseResult.onError(error);
        return this;
    }

    @Override
    public <T> List<T> getResult() {
        return (List<T>) parseResult.getList(returnType);
    }

    /**
     * 返回一个成功了的异步result
     * @param res 返回值
     * @param returnType 类型
     * @param <T> 类型
     * @return 成功了的异步result
     */
    public static <T> ListGenericitySyncResult<T> asSuccess(Object res, Class<T> returnType) {
        DefaultListGenericitySyncResult<T> result = new DefaultListGenericitySyncResult<>(new DefaultSyncParseResult(null), returnType);
        result.parseResult.success(JSON.toJSONString(res));
        return result;
    }

    /**
     * 返回一个失败了的异步result
     * @param exception 异常
     * @param returnType 类型
     * @param <T> 类型
     * @return 失败了的异步result
     */
    public static <T> ListGenericitySyncResult<T> asError(RuntimeException exception, Class<T> returnType) {
        DefaultListGenericitySyncResult<T> result = new DefaultListGenericitySyncResult<>(new DefaultSyncParseResult(null), returnType);
        result.parseResult.error(exception);
        return result;
    }


}
