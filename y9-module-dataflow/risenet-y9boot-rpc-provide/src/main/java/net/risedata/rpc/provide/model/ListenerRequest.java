package net.risedata.rpc.provide.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @Description : 监听发送到包
 * @ClassName ListenerRequest
 * @Author lb
 * @Date 2021/11/24 15:59
 * @Version 1.0
 */
public class ListenerRequest {
    /**
     * 映射
     */
    private String mapping;
    /**
     * 参数
     */
    private Map<String,Object> args;

    /**
     * 监听发送的id
     */
    private long id;
    /**
     * 是否返回
     */
    private boolean isBack;

    public boolean isBack() {
        return isBack;
    }

    public void setBack(boolean back) {
        isBack = back;
    }

    public String getMapping() {
        return mapping;
    }

    public void setMapping(String mapping) {
        this.mapping = mapping;
    }

    public Map<String,Object> getArgs() {
        return args;
    }

    public void setArgs(Map<String,Object> args) {
        this.args = args;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
