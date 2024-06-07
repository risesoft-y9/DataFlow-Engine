package net.risedata.rpc.consumer.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 连接配置通过此类可以动态添加类 建议使用yml配置文件的方式
 * @Author lb176
 * @Date 2021/4/29==16:03
 */
public class ConnectionConfig {

    private Map<String, List<String>> configs = new HashMap<>();

    /**
     * 添加一个连接管理器
     *
     * @param id   id 这个连接管理器的id
     * @param urls urls 连接管理器所管理的url
     */
    public void addConfig(String id, List<String> urls) {
        configs.put(id, urls);
    }

    public Map<String, List<String>> getConfigs() {
        return configs;
    }


}
