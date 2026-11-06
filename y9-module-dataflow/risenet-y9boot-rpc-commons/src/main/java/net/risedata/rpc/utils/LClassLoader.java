package net.risedata.rpc.utils;

import java.util.List;

/**
 * loaderclass
 * 
 * @author libo 2020年7月15日
 */
@SuppressWarnings("rawtypes")
public class LClassLoader {

    public static List<Class<?>> findClass(String packageName) {

        return ClassTools.getClasses(packageName);

    }

}