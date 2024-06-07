package net.risedata.rpc.consumer.listener;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import net.risedata.rpc.utils.LParameter;

/**
 * @Description : 监听器model
 * @ClassName ListenerModel
 * @Author lb
 * @Date 2021/11/24 15:35
 * @Version 1.0
 */
public class ListenerModel {
    /**
     * 实例
     */
    private Object instance;
    /**
     * 参数
     */
    private LParameter[] args;

    /**
     * 方法
     */
    private Method method;

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public LParameter[] getArgs() {
        return args;
    }

    public void setArgs(LParameter[] args) {

        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                this.args = null;
                return;
            }
        }
        this.args = args;
    }


    public void setMethod(Method method) {
        this.method = method;
    }

    public Method getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return "ListenerModel{" +
                "instance=" + instance +
                ", args=" + Arrays.toString(args) +
                ", method=" + method +
                '}';
    }
}
