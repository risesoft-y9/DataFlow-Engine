package net.risedata.rpc.consumer.model;

import com.alibaba.fastjson.JSONObject;

import net.risedata.rpc.utils.IdUtils;

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
    private JSONObject args;
    /**
     * 是否返回
     */
    private boolean isBack;

    /**
     * 监听发送的id
     */
    private long id;


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

    public JSONObject getArgs() {
        return args;
    }

    public void setArgs(JSONObject args) {
        this.args = args;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "ListenerRequest{" +
                "mapping='" + mapping + '\'' +
                ", args=" + args +
                ", isBack=" + isBack +
                ", id=" + id +
                '}';
    }
}
