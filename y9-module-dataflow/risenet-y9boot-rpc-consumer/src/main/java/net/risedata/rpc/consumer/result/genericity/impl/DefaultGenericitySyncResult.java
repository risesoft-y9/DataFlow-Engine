package net.risedata.rpc.consumer.result.genericity.impl;

import com.alibaba.fastjson.JSON;

import net.risedata.rpc.consumer.result.Error;
import net.risedata.rpc.consumer.result.genericity.GenericitySuccess;
import net.risedata.rpc.consumer.result.genericity.GenericitySyncResult;
import net.risedata.rpc.consumer.result.impl.DefaultSyncParseResult;

/**
 * @description: 默认的异步操作result类
 * @Author lb176
 * @Date 2021/8/4==11:45
 */
public class DefaultGenericitySyncResult<T> implements GenericitySyncResult<T> {

    private DefaultSyncParseResult parseResult;


    private Class<T> returnType;

    public DefaultGenericitySyncResult(DefaultSyncParseResult parseResult, Class<T> returnType) {
        this.parseResult = parseResult;
        this.returnType = returnType;
    }

    @Override
    public GenericitySyncResult onSuccess(GenericitySuccess<T> success) {
        parseResult.onSuccess((res) -> {
            success.success(res.getValue(returnType));
        });
        return this;
    }

    @Override
    public GenericitySyncResult onError(Error error) {
        parseResult.onError(error);
        return this;
    }


    public static <T> GenericitySyncResult<T> asSuccess(Object res, Class<T> returnType) {
        DefaultGenericitySyncResult<T> result = new DefaultGenericitySyncResult<>(new DefaultSyncParseResult(null), returnType);
        result.parseResult.success(JSON.toJSONString(res));
        return result;
    }


    public static <T> GenericitySyncResult<T> asError(RuntimeException exception, Class<T> returnType) {
        DefaultGenericitySyncResult<T> result = new DefaultGenericitySyncResult<>(new DefaultSyncParseResult(null), returnType);
        result.parseResult.error(exception);
        return result;
    }

    @Override
    public <T> T getResult() {
        return (T) parseResult.getResult().getValue(returnType);
    }
}
