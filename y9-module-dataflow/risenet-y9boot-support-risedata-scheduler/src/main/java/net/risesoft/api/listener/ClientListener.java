package net.risesoft.api.listener;

import net.risedata.jdbc.executor.search.SearchExecutor;
import net.risedata.rpc.provide.listener.Listener;
import net.risedata.rpc.provide.net.ClinetConnection;
import net.risesoft.api.api.RegisterApi;
import net.risesoft.api.persistence.iservice.IServiceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description : 监听连接的建立
 * @ClassName ClientListener
 * @Author lb
 * @Date 2021/11/25 17:08
 * @Version 1.0
 */
public class ClientListener implements Listener {


    private static final Logger LOG = LoggerFactory.getLogger(ClientListener.class);

    public static final List<ClinetConnection> connections = new ArrayList<>();



    @Override
    public void onConnection(ClinetConnection clinetConnection) {
        if (LOG.isInfoEnabled()) {
            LOG.info(clinetConnection.getRemoteAddress() + "connection ");
        }

        synchronized (connections) {
            connections.add(clinetConnection);
        }
    }

    @Autowired
    IServiceService iServiceService;

    //判断连接数量有几个连接
    @Override
    public void onClosed(ClinetConnection clinetConnection) {

        if (LOG.isInfoEnabled()) {
            LOG.info(clinetConnection.getRemoteAddress() + "连接关闭 ");
        }
        synchronized (connections) {
            connections.remove(clinetConnection);
        }

        Object name = clinetConnection.getAttribute(RegisterApi.CONNECTION_INSTANCE_NAME);
        Object id = clinetConnection.getAttribute(RegisterApi.CONNECTION_INSTANCE_ID);
        if (id != null && name != null) {
            iServiceService.instanceClose(id.toString());
        }
    }

    public static ClinetConnection getConnection(String instanceId) {
        synchronized (connections) {
            for (ClinetConnection connection : connections) {
                if (instanceId.equals(connection.getAttribute(RegisterApi.CONNECTION_INSTANCE_ID))) {
                    return connection;
                }
            }
        }
        return null;
    }

    public static void pushListener(String name, Map<String, Object> value, String serviceName, String instanceId, String environment) {
        Map<Object, Object> sendedMap = new HashMap<>();
        synchronized (connections) {
            for (ClinetConnection connection : connections) {
                try {
                    if (environment != null) {
                        if (!environment.equals(connection.getAttribute(RegisterApi.INSTANCE_ENVIRONMENT_NAME))) {
                            continue;
                        }
                    }
                    if (serviceName != null) {
                        if (!serviceName.equals(connection.getAttribute(RegisterApi.CONNECTION_INSTANCE_NAME))) {
                            continue;
                        }
                    }
                    if (instanceId != null) {
                        if (!instanceId.equals(connection.getAttribute(RegisterApi.CONNECTION_INSTANCE_ID))) {
                            continue;
                        }
                    }
                    if (sendedMap.containsKey(connection.getAttribute(RegisterApi.CONNECTION_INSTANCE_ID))) {
                        continue;
                    }
                    sendedMap.put(connection.getAttribute(RegisterApi.CONNECTION_INSTANCE_ID), true);
                    connection.pushListener(name, value);
                } catch (Exception e) {
                    e.printStackTrace();
                    LOG.error("发送事件失败:" + name + e.getMessage());
                }
            }
        }

    }
}
