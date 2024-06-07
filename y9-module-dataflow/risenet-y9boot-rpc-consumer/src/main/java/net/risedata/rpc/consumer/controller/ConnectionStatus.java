package net.risedata.rpc.consumer.controller;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risedata.rpc.consumer.core.Connection;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.factory.ConnectionManagerFactory;

import java.util.*;

/**
 * @description: 返回连接的数据控制器
 * @Author lb176
 * @Date 2021/8/5==10:25
 */
@RequestMapping("/rpc")
@RestController
public class ConnectionStatus {

    @RequestMapping({"/status/{serviceName}", "/status"})
    public Object status(@PathVariable(required = false) String serviceName) {
        if (!StringUtils.isEmpty(serviceName)) {
            return getDetail(serviceName);
        } else {
            Set<String> keys = ConnectionManagerFactory.getInstances().keySet();
            List<Map<String, Object>> res = new ArrayList<>();
            for (String key : keys
            ) {
                res.add(getDetail(key));
            }
            return res;
        }
    }

    private Map<String, Object> getDetail(String name) {
        Map<String, Object> statusMap = new HashMap<>();
        ConnectionManager manager = ConnectionManagerFactory.getInstance(name);
        statusMap.put("serviceName", name);
        if (manager == null) {
            statusMap.put("connection", " null");
            return statusMap;
        }
        List<Connection> connections = manager.getConnectionPool().getConnections();
        statusMap.put("connectionSize", connections.size());
        statusMap.put("connections", connections);
        return statusMap;
    }
}
