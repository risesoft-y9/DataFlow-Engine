package net.risesoft.api.utils;

/**
 * @Description :
 * @ClassName ObjectUtils
 * @Author lb
 * @Date 2022/9/19 10:19
 * @Version 1.0
 */
public class ObjectUtils {

    public static Object nullOf(Object value, Object defaultValue) {
        return value == null ? defaultValue : value;
    }
}
