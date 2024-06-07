package net.risedata.rpc.consumer.config;


import net.risedata.rpc.Task.ScheduleTask;
import net.risedata.rpc.consumer.config.model.Managers;
import net.risedata.rpc.consumer.controller.ConnectionStatus;
import net.risedata.rpc.consumer.core.impl.ClinetBootStrap;
import net.risedata.rpc.consumer.factory.ClinetBeanFactory;
import net.risedata.rpc.consumer.factory.ConnectionManagerFactory;
import net.risedata.rpc.consumer.invoke.impl.ForBooleanTrueInvokeHandle;
import net.risedata.rpc.consumer.listener.ListenerApplication;
import net.risedata.rpc.service.RPCExecutorService;
import net.risedata.rpc.service.patientia.FixedExecutorService;
import net.risedata.rpc.utils.IdUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.PropertyResolver;
import org.springframework.web.client.RestTemplate;

/**
 * @description: consumer 全局配置
 * @Author lb176
 * @Date 2021/4/29==16:02
 */
@Configuration
@Order(value = 1)
public class ConsumerApplication implements ApplicationListener {
    public static final RestTemplate REST_TEMPLATE = new RestTemplate();
    public static final Logger LOGGER = LoggerFactory.getLogger(ConsumerApplication.class);
    public static ApplicationContext applicationContext;
    public static ScheduleTask scheduleTask;
    public static PropertyResolver propertyResolver;
    public static ClinetConfig CLINET_CONFIG;
    public static RPCExecutorService EXECUTOR_SERVICE;


    private ClinetBootStrap clinetBootStrap;

    @Bean
    public ClinetBeanFactory getClinetBeanFactory() {
        return new ClinetBeanFactory();
    }


    @Bean
    public ForBooleanTrueInvokeHandle getForBooleanTrueInvokeHandle() {
        return new ForBooleanTrueInvokeHandle();
    }

    @Bean
    public LoadingConfig getLoadingConfig() {
        return new LoadingConfig();
    }

    @ConditionalOnMissingBean(ClinetBootStrap.class)
    @Bean
    public ClinetBootStrap getClinetBootStrap() {

        return new ClinetBootStrap();
    }

    @Bean
    public ClinetConfig getClinetConfig() {
        return new ClinetConfig();
    }

    @Bean
    public Managers getManagers() {
        return new Managers();
    }

    @Bean
    @ConditionalOnMissingBean(RPCExecutorService.class)
    RPCExecutorService getExecutorService(ClinetConfig clinetConfig) {
        return new FixedExecutorService(clinetConfig.executorSize);
    }

    @Bean
    public ScheduleTask scheduleTask(ClinetConfig clinetConfig) {
        CLINET_CONFIG = clinetConfig;
        return new ScheduleTask(clinetConfig.refreshTime);
    }

    @ConditionalOnMissingBean(ConnectionConfig.class)
    @Bean
    public ConnectionConfig getConnectionConfig() {
        return new ConnectionConfig();
    }
    @ConditionalOnMissingBean(ConnectionStatus.class)
    @Bean
    public ConnectionStatus getConnectionStatus() {
        return new ConnectionStatus();
    }

    @Bean
    public ConnectionManagerFactory getConnectionManagerFactory() {
        return new ConnectionManagerFactory();
    }


    @Bean
    public ListenerApplication getListenerApplication() {
        return new ListenerApplication();
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {

        if (event instanceof ContextRefreshedEvent) {
            if (scheduleTask != null) {
                return;
            }
            IdUtils.refresh();
            LOGGER.info("start scheduleTask");
            clinetBootStrap = applicationContext.getBean(ClinetBootStrap.class);
            scheduleTask = applicationContext.getBean(ScheduleTask.class);
            EXECUTOR_SERVICE = applicationContext.getBean(RPCExecutorService.class);
        } else if (event instanceof ContextClosedEvent) {
            LOGGER.info("rpc-consumer close");

            EXECUTOR_SERVICE = null;
            ListenerApplication.close();
            ConnectionManagerFactory.close();

            if (clinetBootStrap != null) {
                LOGGER.info("clinetBootStrap close");
                clinetBootStrap.close();
            }
            if (scheduleTask != null) {
                LOGGER.info("scheduleTask close");
                scheduleTask.stop();
            }
            scheduleTask = null;
        }


    }


    public static boolean isInfo() {
        return LOGGER.isInfoEnabled();
    }

}


