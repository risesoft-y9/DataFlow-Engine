package net.risedata.rpc.factory;

import com.alibaba.fastjson.JSONObject;

import net.risedata.rpc.exceptions.ProxyException;
import net.risedata.rpc.factory.model.ReturnType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;


/**
 * 构建方法返回值的工厂
 *
 * @author libo 2021年7月8日
 */
public class ReturnTypeFactory {
    /**
     * 解析方法拿到返回值对象
     *
     * @param m
     * @return
     */
    public static ReturnType parseInstance(Method m) {
        String genericityName = m.getGenericReturnType().getTypeName();
        Class<?> returnType = m.getReturnType();
        if (returnType == Object.class) {
            if (!genericityName.equals("java.lang.Object")) {
                int index = findT(genericityName, m);
                if (index != -1) {
                    return new ReturnType(returnType, null, false, index,false);
                }
            }else{
                return new ReturnType(JSONObject.class,null,false,0,true);
            }
            throw new ProxyException("An undefined type " + genericityName);

        }
        if (!genericityName.startsWith("java.util.Map")) {
            int start = genericityName.indexOf("<");
            int end = start == -1 ? -1 : genericityName.lastIndexOf(">");
            if (start != -1 && end != -1) {
                genericityName = genericityName.substring(start + 1, end);
                int index = findT(genericityName, m);
                if (index == -1) {
                    start = genericityName.indexOf("<");
                    if (start != -1) {
                        genericityName = genericityName.substring(0, start);
                    }
                    try {
                        Class<?> genericityClass = Class.forName(genericityName);
                        return new ReturnType(returnType, genericityClass);
                    } catch (ClassNotFoundException e) {
                        throw new ProxyException("class not found " + e.getMessage());
                    }
                }
                return new ReturnType(returnType, null, false, index,true);
            }
        } else {
            return new ReturnType(returnType, Map.class);
        }

        return new ReturnType(returnType);
    }

    /**
     * 拿到泛型T 对应class
     *
     * @param genericityName
     * @param m
     * @return
     */
    private static int findT(String genericityName, Method m) {
        Parameter[] ps = m.getParameters();
        for (int i = 0; i < ps.length; i++) {
            if (ps[i].getParameterizedType().getTypeName().contains("<" + genericityName + ">")) {
                return i;
            }
        }
        return -1;
    }

}
