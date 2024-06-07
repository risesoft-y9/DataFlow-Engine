package net.risedata.rpc.provide.config;

import net.risedata.rpc.provide.annotation.RPCServer;
import net.risedata.rpc.provide.annotation.TypeConvert;
import net.risedata.rpc.provide.controller.ProvideController;
import net.risedata.rpc.provide.defined.ClassDefined;
import net.risedata.rpc.provide.defined.MethodDefined;
import net.risedata.rpc.provide.exceptions.MatchException;
import net.risedata.rpc.provide.filter.FilterContext;
import net.risedata.rpc.provide.filter.FilterContextCache;
import net.risedata.rpc.provide.filter.FilterManager;
import net.risedata.rpc.provide.handle.ParameterHandle;
import net.risedata.rpc.provide.handle.TypeConvertHandle;
import net.risedata.rpc.provide.handle.impl.DefaultTypeConvertHandle;
import net.risedata.rpc.provide.handle.impl.RequestTypeConver;
import net.risedata.rpc.provide.listener.Listener;
import net.risedata.rpc.provide.net.ClinetConnection;
import net.risedata.rpc.provide.net.Server;
import net.risedata.rpc.service.RPCExecutorService;
import net.risedata.rpc.service.patientia.FixedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import java.util.*;

/**
 * @description: 全局配置
 * @Author lb176
 * @Date 2021/4/29==16:28
 */
public class ApplicationConfig implements ApplicationListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationConfig.class);

	/**
	 * 处理器集合
	 */
	protected static final Map<String, ClassDefined> PROVIDES = new HashMap<>();
	/**
	 * 映射
	 */
	protected static final Map<String, MethodDefined> MAPPING = new HashMap<>();

	public static ApplicationContext applicationContext;
	/**
	 * 全局处理类型默认handle
	 */
	public static final TypeConvertHandle DEFAULT_TYPE_CONVERT_HANDLE = new DefaultTypeConvertHandle();

	/**
	 * 加载参数处理链
	 */
	public static final List<ParameterHandle> PARAMETER_HANDLES = new ArrayList<>();
	/**
	 * 当前的全局server
	 */
	private static Server server;
	/**
	 * 配置的参数
	 */
	public static ConfigArgs configArgs;

	private static final RequestTypeConver REQUEST_TYPE_CONVER = new RequestTypeConver();

	@Bean
	@ConditionalOnMissingBean(ConfigArgs.class)
	ConfigArgs configArgs() {

		return new ConfigArgs();
	}

	@Bean
	@ConditionalOnMissingBean(RPCExecutorService.class)
	RPCExecutorService getExecutorService(ConfigArgs args) {
		return new FixedExecutorService(args.executorSize);
	}

	@Bean
	@ConditionalOnMissingBean(ProvideController.class)
	ProvideController getProvideController() {
		return new ProvideController();
	}

	@Bean
	FilterManager filterManager() {
		return new FilterManager();
	}

	@Bean
	ServerBeanFactory serverBeanFactory() {
		return new ServerBeanFactory();
	}

	/**
	 * 获取class的定义
	 *
	 * @param key
	 * @return
	 */
	public static ClassDefined getClassDefined(String key) {
		return PROVIDES.get(key);
	}

	public static void putMapping(String mapping, MethodDefined methodDefined) {
		if (MAPPING.containsKey(mapping)) {
			throw new MatchException(mapping + " already exist ");
		}
		MAPPING.put(mapping, methodDefined);
	}

	/**
	 * 根据映射拿到定义
	 * 
	 * @param mapping
	 * @return
	 */
	public static MethodDefined getMethodDefined(String mapping) {

		return MAPPING.get(mapping);
	}

	/**
	 * 拿到当前的所有服务提供者
	 *
	 * @return
	 */
	public static Map<String, ClassDefined> getProvides() {
		return PROVIDES;
	}

	/**
	 * 拿到当前的所有服务提供者
	 *
	 * @return
	 */
	public static Map<String, MethodDefined> getMaaping() {
		return MAPPING;
	}

	/**
	 * 将自定义的参数处理器添加到参数处理链中
	 *
	 * @param p
	 */
	public static void addParameterHandle(ParameterHandle p) {
		PARAMETER_HANDLES.add(1, p);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {

		if (event instanceof ContextRefreshedEvent) {
			LOGGER.info("start rpc");
			ContextRefreshedEvent event1 = (ContextRefreshedEvent) event;
			if (applicationContext != null) {
				return;
			}
			applicationContext = event1.getApplicationContext();
			configArgs = applicationContext.getBean(ConfigArgs.class);
			ClinetConnection.EXECUTOR_SERVICE = applicationContext.getBean(RPCExecutorService.class);

			List<TypeConvertHandle> allTypeConverts = new ArrayList<>();
			String[] typeConverts = applicationContext.getBeanNamesForType(TypeConvertHandle.class);
			for (String convert : typeConverts) {
				TypeConvertHandle bean = (TypeConvertHandle) applicationContext.getBean(convert);
				TypeConvert typeConvert = AnnotationUtils.findAnnotation(bean.getClass(), TypeConvert.class);
				if (typeConvert != null) {
					if (typeConvert.value().length > 0) {
						for (String apiName : typeConvert.value()) {
							ClassDefined classDefined = PROVIDES.get(apiName);
							if (classDefined == null) {
								throw new MatchException("provide is null " + apiName);
							}
							classDefined.addTypeConvertHandle(bean);
						}
					} else {
						allTypeConverts.add(bean);
					}
				}
			}
			RPCServer rpcServer = null;
			for (ClassDefined classDefined : PROVIDES.values()) {
				if (allTypeConverts.size() > 0) {

					allTypeConverts.forEach(classDefined::addTypeConvertHandle);
				}
				classDefined.setInstance(applicationContext.getBean(classDefined.getType()));
				rpcServer = AnnotationUtils.findAnnotation(classDefined.getType(), RPCServer.class);
				if (rpcServer != null && rpcServer.enableRequest()) {
					classDefined.addTypeConvertHandle(REQUEST_TYPE_CONVER);
				}
			}
			server = new Server(configArgs.port, configArgs.workSize);
			Map<String, Listener> listeners = applicationContext.getBeansOfType(Listener.class);
			for (Listener filter : listeners.values()) {
				server.getlistenerManager().addListener(filter);
			}
			server.start();
		} else if (event instanceof ContextClosedEvent) {
			// 应用关闭
			LOGGER.info("close rpc");
			PROVIDES.clear();
			MAPPING.clear();
			PARAMETER_HANDLES.clear();
			FilterManager.clear();
			FilterContext.refresh();
			server.close();
		}

	}

}
