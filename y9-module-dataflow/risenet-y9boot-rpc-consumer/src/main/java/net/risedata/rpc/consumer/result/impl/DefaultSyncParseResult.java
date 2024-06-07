package net.risedata.rpc.consumer.result.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.risedata.rpc.consumer.exceptions.RpcException;
import net.risedata.rpc.consumer.result.*;
import net.risedata.rpc.consumer.result.Error;
import net.risedata.rpc.consumer.result.genericity.GenericitySyncResult;
import net.risedata.rpc.consumer.result.genericity.ListGenericitySyncResult;
import net.risedata.rpc.consumer.result.genericity.impl.DefaultGenericitySyncResult;
import net.risedata.rpc.consumer.result.genericity.impl.DefaultListGenericitySyncResult;
import net.risedata.rpc.model.Request;

import java.util.*;

/**
 * @description: 默认操作的异步操作实现类
 * @Author lb176
 * @Date 2021/7/30==9:50
 */
public class DefaultSyncParseResult implements SyncParseResult, Result {

    /**
     * 成功后调用的接口
     */
    private Success success;
    /**
     * 异常
     */
    private Error eror;
    /**
     * 返回值
     */
    private String res;
    /**
     * 请求对象
     */
    private Request request;
    /**
     * 异常
     */
    private RuntimeException exception;

    public DefaultSyncParseResult(Request request) {
        this.request = request;
    }

    //类型处理以及result 处理
    @Override
    public void success(String res) {
        this.res = res;
        if (this.success != null) {
            this.success.success(this);
        }
    }

    @Override
    public void error(Throwable e) {
        this.exception = (RuntimeException) e;
        if (this.eror != null) {
            this.eror.error(request, e);
        }
    }


    @Override
    public Result getResult() {
        if (exception != null) {
            throw exception;
        }
        if (res != null) {
            return this;
        }
        Thread thread = Thread.currentThread();
        this.onSuccess((t) -> {
            synchronized (thread) {
                thread.notify();
            }
        }).onError((error, e) -> {
            exception = (RuntimeException) e;
            synchronized (thread) {
                thread.notify();
            }
        });

        try {
            synchronized (thread) {
                if (exception != null) {
                    throw exception;
                }
                if (res != null) {
                    return this;
                }
                thread.wait();
            }
        } catch (InterruptedException e) {
            throw new RpcException(e.getMessage());
        }
        if (res == null) {
            throw exception;
        }
        return this;
    }

    @Override
    public SyncResult onSuccess(Success success) {
        this.success = success;
        if (res != null) {
            success.success(this);
        }
        return this;
    }

    @Override
    public SyncResult onError(Error error) {
        this.eror = error;
        if (exception != null) {
            error((Throwable) exception);
        }
        return this;
    }

    @Override
    public <T> ListGenericitySyncResult<T> asList(Class<T> returnType) {
        return new DefaultListGenericitySyncResult<T>(this, returnType);
    }

    @Override
    public <T> GenericitySyncResult<T> as(Class<T> returnType) {
        return new DefaultGenericitySyncResult<>(this, returnType);
    }

    @Override
    public <T> T getValue(Class<T> returnType) {
        return JSON.parseObject(res, returnType);
    }


    public Object toMap() {
        if (JSON.isValidArray(res)) {
            List<Object> lists = JSONArray.parseArray(res, Object.class);
            Object temp;
            Object temp2;
            for (int i = 0; i < lists.size(); i++) {
                temp = lists.get(i);
                temp2 = getObject(temp);
                if (temp2 != temp) {
                    lists.set(i, temp);
                }
            }
            return lists;
        } else {
            Map<String, Object> map = JSON.parseObject(res, Map.class);
            map.forEach((k, v) -> {
                Object v2 = getObject(v);
                if (v2 != v) {
                    map.put(k, v2);
                }
            });
            return map;
        }


    }


    private Object getObject(Object v) {
        if (v == null) {
            return null;
        }
        if (isJson(v.getClass())) {
            Map<String, Object> data = ((JSONObject) v).getInnerMap();
            data.forEach((k, v2) -> {
                Object v3 = getObject(v2);
                if (v3 != null && v3 != v2) {
                    data.put(k, v3);
                }
            });
            return data;
        }
        if (isJsonArray(v.getClass())) {
            List<Object> list = Arrays.asList(((JSONArray) v).toArray());
            Object temp = null;
            Object temp2 = null;
            for (int i = 0; i < list.size(); i++) {
                temp = list.get(i);
                if (temp != null) {
                    temp2 = null;
                    if (isJson(temp.getClass())) {
                        temp2 = getObject(temp);
                    } else if (isJsonArray(temp.getClass())) {
                        temp2 = getObject(temp);
                    }
                    if (temp2 != null && temp != temp2) {
                        list.set(i, temp2);
                    }
                }

            }
            return list;
        }
        return v;
    }


    private boolean isJson(Class<?> type) {
        return JSONObject.class == type;
    }

    private boolean isJsonArray(Class<?> type) {
        return JSONArray.class == type;
    }

    private boolean isMap(Class<?> returnType) {
        return returnType == Object.class || Map.class.isAssignableFrom(returnType);
    }

    @Override
    public <T> List<T> getList(Class<T> returnType) {
        JSONArray jsonArray = JSON.parseArray(res);
        return jsonArray.toJavaList(returnType);
    }

    /**
     * 返回一个成功了的异步操作
     *
     * @param res 返回值
     * @return
     */
    public static SyncResult asSucces(Object res) {
        DefaultSyncParseResult defaultSyncParseResult = new DefaultSyncParseResult(null);
        defaultSyncParseResult.success(JSON.toJSONString(res));
        return defaultSyncParseResult;
    }

    /**
     * 返回一个失败了的异步操作
     *
     * @param e 异常
     * @return
     */
    public static SyncResult asError(RuntimeException e) {
        DefaultSyncParseResult defaultSyncParseResult = new DefaultSyncParseResult(null);
        defaultSyncParseResult.error(e);
        return defaultSyncParseResult;
    }
}
