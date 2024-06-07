package net.risedata.rpc.consumer.factory;

import net.risedata.rpc.annotation.RPCScan;
import net.risedata.rpc.consumer.annotation.RPCClinet;
import net.risedata.rpc.consumer.config.ConsumerApplication;
import net.risedata.rpc.consumer.config.LoadingConfig;
import net.risedata.rpc.consumer.core.BasedConnectionManager;
import net.risedata.rpc.consumer.core.ConnectionManager;
import net.risedata.rpc.consumer.invoke.ProxyInvoke;
import net.risedata.rpc.consumer.listener.ExceptionListener;
import net.risedata.rpc.consumer.utils.ConsumerUtils;
import net.risedata.rpc.provide.config.Application;
import net.risedata.rpc.utils.LClassLoader;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.PropertyResolver;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @description: 此工厂用于实现预加载的bean的加载
 * @Author lb176
 * @Date 2021/4/29==15:11
 */
public class ClinetBeanFactory implements BeanFactoryPostProcessor, ApplicationContextAware {
    public static Map<Class, ProxyInvoke> invokeMap;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        List<Class> rpcClass = getRPCClinetedClass(ConsumerApplication.applicationContext);
        Application.logger.info(" proxy  add " + rpcClass.size());
        RPCClinet rpcClinet;
        ProxyInvoke proxyInvoke;
        Object proxy;
        DefaultListableBeanFactory factory = (DefaultListableBeanFactory) ConsumerApplication.applicationContext.getAutowireCapableBeanFactory();
        invokeMap = new HashMap<>(rpcClass.size());
        BeanDefinition beanDefinition;
        String apiName;
        String managerName;
        String managers = ConsumerApplication.propertyResolver.getProperty("rpc.manager");
        if (!StringUtils.isEmpty(managers)) {
            String[] names = managers.split(",");
            for (String name : names) {
                registerManager(beanFactory, LoadingConfig.getNullConnectionManager(), name, null);
            }
        }
        for (Class type : rpcClass) {
            rpcClinet = AnnotationUtils.findAnnotation(type, RPCClinet.class);
            apiName = StringUtils.isEmpty(rpcClinet.name()) ? type.getName() : ConsumerUtils.getValue(rpcClinet.name());
            proxyInvoke = new ProxyInvoke(apiName);
            proxy = ProxyFactory.getProxy(new Class[]{type}, proxyInvoke);
            beanFactory.registerResolvableDependency(type, proxy);
            //将熔断的bean 也加载到工厂中
            if (rpcClinet.degrade() != ExceptionListener.class) {
                beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClassName(rpcClinet.degrade().getName());
                beanDefinition.setAutowireCandidate(false);
                factory.registerBeanDefinition(rpcClinet.degrade().getName(), beanDefinition);
            }
            invokeMap.put(type, proxyInvoke);
            managerName = StringUtils.isEmpty(rpcClinet.managerName()) ? type.getName() + "Manager" : ConsumerUtils.getValue(rpcClinet.managerName());
            //加载我自己动态代理的bean
            ConnectionManager connectionManager = ConnectionManagerFactory.getInstance(managerName);
            if (connectionManager == null) {
                connectionManager = LoadingConfig.getNullConnectionManager();
                registerManager(beanFactory, connectionManager, managerName, type);
            }


        }

    }

    /**
     * 将connectionManager 放到spring 工厂中以及自己的工厂
     *
     * @param beanFactory spring 工厂
     * @param manager     manager
     * @param name        对应的名字
     */
    private void registerManager(ConfigurableListableBeanFactory beanFactory, BasedConnectionManager manager, String name, Class<?> cla) {
        ConnectionManagerFactory.registerManage(name, manager);
        if (cla != null) {
            ConnectionManagerFactory.registerManage(cla, (ConnectionManager) manager);
        }
        beanFactory.registerSingleton(name, manager);
    }

    /**
     * 拿到增加了rpc clinet 注解的接口
     *
     * @param applicationContext 上下文
     * @return 增加了rpc clinet 的类合集
     */
    private List<Class> getRPCClinetedClass(ApplicationContext applicationContext) {
        Map<String, Object> roots = applicationContext.getBeansWithAnnotation(RPCScan.class);

        List<Class> scanClass = new ArrayList<>();
        Collection<Object> values = roots.values();
        LoadingConfig.isDiscoveryClient = true;
        try {
            for (Object o : values) {
                RPCScan rpcScan = AnnotationUtils.findAnnotation(o.getClass(), RPCScan.class);
                if (rpcScan != null) {
                    String[] scams = rpcScan.value();
                    for (String scam : scams) {
                        ConsumerApplication.LOGGER.info("load class to scan:" + scam);
                        scanClass.addAll(LClassLoader.findClass(scam));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean isInterface = false;
        boolean isRpcClinet = false;
        for (int i = 0; i < scanClass.size(); i++) {
            isInterface = !scanClass.get(i).isInterface();
            isRpcClinet = AnnotationUtils.findAnnotation(scanClass.get(i), RPCClinet.class) == null;
            if (isInterface || isRpcClinet) {
                scanClass.remove(i);
                i--;
            }
        }
        return scanClass;

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ConsumerApplication.propertyResolver = applicationContext.getBean(PropertyResolver.class);
        ConsumerApplication.applicationContext = applicationContext;

    }
}
