package net.risedata.rpc.provide.defined;



import javassist.NotFoundException;
import net.risedata.rpc.provide.annotation.API;
import net.risedata.rpc.provide.config.ApplicationConfig;
import net.risedata.rpc.provide.exceptions.MatchException;
import net.risedata.rpc.provide.handle.ParameterHandle;
import net.risedata.rpc.provide.handle.TypeConvertHandle;
import net.risedata.rpc.utils.LParameter;
import net.risedata.rpc.utils.MethodUtils;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;


/**
* @description: 保留类的信息
* @Author lb176
* @Date 2021/4/29==16:30
*/
public class ClassDefined {
    /**
     * 实例
     */
    private Object instance;

    private Class<?> type;

    private boolean isTypeConvert = false;
    /**
     * 注解所配置的name
     */
    private String name;

    /**
     * 添加到这个类上面的 type处理器
     */
    private List<TypeConvertHandle> typeConvertHandles;

    public void setType(Class<?> type) {
        this.type = type;
    }

    /**
     * 方法的配置
     */
    private Map<String, MethodDefined> methods = new HashMap<>();

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Map<String, MethodDefined> getMethods() {
        return methods;
    }

    public Class<?> getType() {
        return type;
    }

    public void addTypeConvertHandle(TypeConvertHandle typeConvertHandle) {
        if (typeConvertHandles == null) {
            typeConvertHandles = new ArrayList<>();
            isTypeConvert = true;
        }
        typeConvertHandles.add(typeConvertHandle);
    }

    public boolean isTypeConvert() {
        return isTypeConvert;
    }

    /**
     * 拿到类型处理器
     *
     * @return
     */
    public List<TypeConvertHandle> getTypeConvertHandles() {
        return typeConvertHandles;
    }


    /**
     * 将方法加载到自己的配置中
     *
     * @param method
     */
    public void putMethod(Method method) {
        API api = AnnotationUtils.findAnnotation(method, API.class);
        if (api == null) {
            return;
        }
        String methodName = StringUtils.isEmpty(api.name()) ? method.getName() : api.name();
        if (methods.containsKey(methodName)) {
            throw new MatchException(methodName + " name repetition  ");
        }
        String mapping = name+"/"+methodName;
        MethodDefined methodConfig = new MethodDefined();

        methodConfig.setMethod(method);
        methodConfig.setClassDefined(this);
        method.setAccessible(true);
        Parameter[] parameters = method.getParameters();
        if (parameters.length > 0) {
            List<ParameterDefined> parameterDefineds = new ArrayList<>();
            LParameter[] lParameters = null;
            try {
                lParameters = MethodUtils.getParameters(type, method);
            } catch (NotFoundException e) {
                e.printStackTrace();
            }
            for (LParameter p : lParameters
            ) {

                for (ParameterHandle handle : ApplicationConfig.PARAMETER_HANDLES) {
                    if (handle.isHandle(p)) {
                        parameterDefineds.add(handle.getParameterDefined(p));
                        break;
                    }
                }

            }
            methodConfig.setParameterDefineds(parameterDefineds);
        }
        methods.put(methodName, methodConfig);
        ApplicationConfig.putMapping(mapping,methodConfig);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ClassDefined{" +
                "instance=" + instance +
                ", type=" + type +
                ", isTypeConvert=" + isTypeConvert +
                ", name='" + name + '\'' +
                ", typeConvertHandles=" + typeConvertHandles +
                ", methods=" + methods +
                '}';
    }
}
