package net.risedata.rpc.provide.listener;

import com.alibaba.fastjson.JSONArray;

import java.util.List;

/**
 * @description: 返回值
 * @Author lb176
 * @Date 2021/8/4==11:15
 */
public interface Result {
    /**
     * 根据类型拿到对应的值
     * @return
     */
    JSONArray getValue();

    /**
     * 拿到list 类型的值
     *
     * @param returnType 返回值类型
     * @param <T>        list
     * @return 返回值
     */
    <T> List<T> getList(Class<T> returnType);


}
