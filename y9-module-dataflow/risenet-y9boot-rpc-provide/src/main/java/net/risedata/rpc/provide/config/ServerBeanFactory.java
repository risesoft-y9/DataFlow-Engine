package net.risedata.rpc.provide.config;

import net.risedata.rpc.annotation.RPCScan;
import net.risedata.rpc.provide.annotation.EnableRequestParam;
import net.risedata.rpc.provide.annotation.RPCServer;
import net.risedata.rpc.provide.config.Application;
import net.risedata.rpc.provide.defined.ClassDefined;
import net.risedata.rpc.provide.handle.impl.DefaultParameterHandle;
import net.risedata.rpc.provide.handle.impl.ParamParameterHandle;
import net.risedata.rpc.provide.handle.impl.RequestParamParameterHandle;
import net.risedata.rpc.utils.LClassLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @description: 用于处理bean的提前加载
 * @Author lb176
 * @Date 2021/4/29==16:29
 */
public class ServerBeanFactory implements BeanFactoryPostProcessor, ApplicationContextAware {
    private static ApplicationContext ac;
    public static final Logger LOGGER = LoggerFactory.getLogger(ServerBeanFactory.class);

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        List<Class> rpcClass = getRPCServerClass();
        Application.logger.info("start server factory " + rpcClass.size());
        for (Class aClass : rpcClass) {
            loadConfig(aClass);
        }
    }


    public static void loadConfig(Class<?> loadType) {
        RPCServer rpcServer = AnnotationUtils.findAnnotation(loadType, RPCServer.class);
        if (rpcServer != null) {
            ClassDefined classDefined = new ClassDefined();
            String name = StringUtils.isEmpty(rpcServer.name()) ? loadType.getName() : rpcServer.name();
            Class<?> supClass = loadType;
            if (supClass.getName().contains("$$")) {
                supClass = loadType.getSuperclass();
            }
            classDefined.setName(name);
            classDefined.setType(supClass);
            Method[] declaredMethods = loadType.getDeclaredMethods();
            for (Method method : declaredMethods) {
                classDefined.putMethod(method);
            }
            ApplicationConfig.PROVIDES.put(name, classDefined);
        }
    }

    /**
     * 拿到所有的rpc 服务
     *
     * @return class集合
     */
    private List<Class> getRPCServerClass() {
        Map<String, Object> roots = ac.getBeansWithAnnotation(RPCScan.class);
        List<Class> scanClass = new ArrayList<>();

        try {
            for (Object o : roots.values()) {
                RPCScan rpcScan = AnnotationUtils.findAnnotation(o.getClass(), RPCScan.class);
                if (rpcScan != null) {
                    String[] scams = rpcScan.value();
                    for (String scam : scams) {
                        if (LOGGER.isInfoEnabled()) {

                            LOGGER.info("load :" + scam);
                        }
                        scanClass.addAll(LClassLoader.findClass(scam));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < scanClass.size(); i++) {
            if (AnnotationUtils.findAnnotation(scanClass.get(i), RPCServer.class) == null) {
                scanClass.remove(i);
                i--;
            }
        }
        return scanClass;

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
        ApplicationConfig.PARAMETER_HANDLES.add(new ParamParameterHandle());
        Map<String, Object> roots = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
        for (Object o : roots.values()) {
            if (AnnotationUtils.findAnnotation(o.getClass(), EnableRequestParam.class) != null) {
                ApplicationConfig.addParameterHandle(new RequestParamParameterHandle());
                break;
            }
        }
        ApplicationConfig.PARAMETER_HANDLES.add(new DefaultParameterHandle());
    }
}
