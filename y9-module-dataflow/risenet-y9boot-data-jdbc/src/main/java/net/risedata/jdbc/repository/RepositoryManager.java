package net.risedata.jdbc.repository;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;

import net.risedata.jdbc.commons.LogTime;
import net.risedata.jdbc.commons.utils.ClassTools;
import net.risedata.jdbc.config.EnableRepository;
import net.risedata.jdbc.factory.DynamicProxyFactory;

@Configuration

public class RepositoryManager implements BeanFactoryPostProcessor {

	private static final Logger Log = LoggerFactory.getLogger(RepositoryManager.class);

//	@Override
//	public void onApplicationEvent(ApplicationPreparedEvent event) {
//		RepositoryCreateFactory.startWeb(event.getApplicationContext());
//	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

		String[] bootBean = beanFactory.getBeanNamesForAnnotation(EnableRepository.class);
		int count = 0;
		LogTime.start();
		for (int i = 0; i < bootBean.length; i++) {
			Object boot = beanFactory.getBean(bootBean[i]);
			EnableRepository scan = AnnotationUtils.findAnnotation(boot.getClass(), EnableRepository.class);
			if (scan != null && StringUtils.isNotBlank(scan.value()[0])) {
				String[] paths = scan.value();
				for (String path : paths) {
					Object instance = null;
					List<Class<?>> classs = ClassTools.getClasses(path);
					for (Class<?> clas : classs) {
						if (clas.isInterface() && Repository.class.isAssignableFrom(clas)) {
							// 获取泛型
							count++;
							instance = DynamicProxyFactory.getInstance(clas);
							beanFactory.registerSingleton(clas.getSimpleName(), instance);
							beanFactory.registerResolvableDependency(clas, instance);
						}
					}
				}
			}
		}
		Log.info("repository load " + count + " time:" + LogTime.get() + "ms");
	}

}
