package net.risedata.rpc.consumer.listener;

import javassist.NotFoundException;
import net.risedata.rpc.consumer.annotation.Listener;
import net.risedata.rpc.consumer.annotation.Listeners;
import net.risedata.rpc.consumer.exceptions.ListenerException;
import net.risedata.rpc.utils.LParameter;
import net.risedata.rpc.utils.MethodUtils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 只需要获取监听的配置 method信息以及实例
 *
 * @Description : 监听器相关
 * @ClassName ListenerApplication
 * @Author lb
 * @Date 2021/11/24 11:28
 * @Version 1.0
 */
public class ListenerApplication implements ApplicationListener<ContextRefreshedEvent> {


    public static final Map<String, List<ListenerModel>> LISTENER_MODELS = new HashMap<>();
    private boolean isInit = false;
    public static final List<ListenerBack> LISTENER_BACKS = new ArrayList<>();

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (event.getApplicationContext().getParent() == null || event.getApplicationContext().getParent().getParent() == null) {
            if (isInit) {
                return;
            }
            isInit=true;
            ApplicationContext application = event.getApplicationContext();
            String[] ListenerBeanNames = application.getBeanNamesForAnnotation(Listeners.class);
            Map<String, ListenerBack>  listenerBackMap =   application.getBeansOfType(ListenerBack.class);
            listenerBackMap.forEach((key,value)->{
                LISTENER_BACKS.add(value);
            });
            Object o;
            Class<?> cla;
            Listener listener;
            LParameter[] args;
            ListenerModel listenerModel = null;
            Method[] ms;
            List<ListenerModel> listeners;
            for (String listenerBeanName : ListenerBeanNames) {
                o = application.getBean(listenerBeanName);
                cla = ClassUtils.getUserClass(o);

                ms = cla.getDeclaredMethods();
                for (Method m : ms) {
                    listener = AnnotationUtils.getAnnotation(m, Listener.class);
                    if (listener != null) {
                        listenerModel = new ListenerModel();
                        listenerModel.setInstance(o);
                        listenerModel.setMethod(m);
                        try {
                            args = MethodUtils.getParameters(cla, m);
                        } catch (NotFoundException e) {
                            throw new ListenerException("add listener error" + e.getMessage());
                        }
                        listenerModel.setArgs(args);
                        listeners = LISTENER_MODELS.get(listener.value());
                        if (listeners == null) {
                            listeners = new LinkedList<>();
                            LISTENER_MODELS.put(listener.value(), listeners);
                        }
                        listeners.add(listenerModel);
                    }


                }
            }
        }
    }

    public static void close(){

        LISTENER_MODELS.clear();
    }
}
