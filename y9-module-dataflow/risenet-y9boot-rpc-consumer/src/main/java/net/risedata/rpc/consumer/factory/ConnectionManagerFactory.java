package net.risedata.rpc.consumer.factory;

import net.risedata.rpc.consumer.config.ConsumerApplication;
import net.risedata.rpc.consumer.config.LoadingConfig;
import net.risedata.rpc.consumer.core.BasedConnectionManager;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.exceptions.ConfigException;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 管理着当前项目存在的connectionManager
 * @Author lb176
 * @Date 2021/4/29==15:12
 */
public class ConnectionManagerFactory implements ApplicationContextAware {

    private static  Map<String, BasedConnectionManager> CONNECTION_MANAGER_MAP = new HashMap<>();

    private static LoadingConfig loadingConfig;

    public static ConnectionManager getInstance(String id) {
        return (ConnectionManager) CONNECTION_MANAGER_MAP.get(id);
    }

    public static ConnectionManager createManager() {
        return loadingConfig.getConnectionManager();
    }

    public static void stopManager(BasedConnectionManager connectionManager) {
        connectionManager.stop();
    }

    public static void clearManager(String id) {
        CONNECTION_MANAGER_MAP.remove(id);
    }

    public static void clearManager(Class<?> idClass) {
        CONNECTION_MANAGER_MAP.remove(idClass.getName());
    }

    public static BasedConnectionManager getBasedInstance(String id) {
        return CONNECTION_MANAGER_MAP.get(id);
    }

    public static void registerManage(String id, BasedConnectionManager manager) {
        if (CONNECTION_MANAGER_MAP.containsKey(id)) {
            throw new ConfigException(id + "已存在");
        }
        CONNECTION_MANAGER_MAP.put(id, manager);
    }

    public static void registerManage(Class<?> idClass, ConnectionManager manager) {
        CONNECTION_MANAGER_MAP.put(idClass.getName(), manager);
    }

    public static ConnectionManager getInstance(Class<?> idClass) {
        return getInstance(idClass.getName());
    }

    public static Map<String, BasedConnectionManager> getInstances() {
        return CONNECTION_MANAGER_MAP;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        applicationContext.getBean(LoadingConfig.class);
    }

    public static void close(){
        ConsumerApplication.LOGGER.info("clear CONNECTION_MANAGER_MAP");
        CONNECTION_MANAGER_MAP = new HashMap<>();
    }
}
