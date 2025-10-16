package net.risedata.rpc.provide.start;


import net.risedata.rpc.provide.annotation.RPCServer;
import net.risedata.rpc.provide.config.ApplicationConfig;
import net.risedata.rpc.provide.config.ConfigArgs;
import net.risedata.rpc.provide.config.ServerBeanFactory;
import net.risedata.rpc.provide.defined.ClassDefined;
import net.risedata.rpc.provide.handle.impl.DefaultParameterHandle;
import net.risedata.rpc.provide.handle.impl.ParamParameterHandle;
import net.risedata.rpc.provide.listener.Listener;
import net.risedata.rpc.provide.net.ClientConnection;
import net.risedata.rpc.provide.net.Server;
import net.risedata.rpc.service.patientia.FixedExecutorService;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

/**
 * @description: 在没有spring boot 的环境下启动服务端
 * @Author lb176
 * @Date 2021/4/29==16:32
 */
public class ServerBootStart extends ApplicationConfig {
    private static Server server;
    private static ServerApplicationContext applicationContext;

    public static void close() {
        server.close();
    }

    public static void iniApplication(int port) {
        server = new Server(port, 0);
        applicationContext = new ServerApplicationContext();
        ApplicationConfig.applicationContext = applicationContext;
        ApplicationConfig.configArgs = new ConfigArgs();
        PARAMETER_HANDLES.add(new ParamParameterHandle());
        PARAMETER_HANDLES.add(new DefaultParameterHandle());
        ClientConnection.EXECUTOR_SERVICE = new FixedExecutorService(10);

    }

    public static void start(){
        server.start();
    }

    public static void addService(Class<?> serviceClass, Object bean) {

        RPCServer rpcServer = AnnotationUtils.findAnnotation(serviceClass, RPCServer.class);
        if (rpcServer != null) {
            ClassDefined classDefined = new ClassDefined();
            String name = StringUtils.isEmpty(rpcServer.name()) ? serviceClass.getName() : rpcServer.name();
            ServerBeanFactory.loadConfig(serviceClass);
            applicationContext.addBean(serviceClass.getName(), bean);
            ClassDefined classDefined1 = PROVIDES.get(name);
            classDefined1.setInstance(bean);
        }

    }

    public static void addListener(Listener filter) {
        server.getlistenerManager().addListener(filter);
    }
}
