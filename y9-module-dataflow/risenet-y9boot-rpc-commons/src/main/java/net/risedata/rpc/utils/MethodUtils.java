package net.risedata.rpc.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import net.risedata.rpc.exceptions.ConfigException;

public class MethodUtils {
    /**
     * 拿到一个对象所有的参数
     *
     * @param clazz
     * @param m
     * @return
     * @throws NotFoundException
     */
    public static LParameter[] getParameters(Class<?> clazz, Method m) throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        Class<?>[] types = m.getParameterTypes();
        Annotation[][] as = new Annotation[types.length][];
        Class<?>[][] generalType = new Class[types.length][];
        java.lang.reflect.Parameter[] ps = m.getParameters();
        for (int i = 0; i < ps.length; i++) {
            as[i] = ps[i].getAnnotations();
            generalType[i] = getGeneralTypes(ps[i].getParameterizedType().getTypeName());
        }
        pool.insertClassPath(new ClassClassPath(clazz));
        CtClass cc = pool.get(clazz.getName());
        CtMethod cm = cc.getDeclaredMethod(m.getName());
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr =
                (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            return null;
        }
        String[] parameterNames = new String[cm.getParameterTypes().length];
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        LParameter[] pss = new LParameter[cm.getParameterTypes().length];
        LParameter p = null;
        String pname;
        for (int i = 0; i < attr.tableLength(); i++) {
            if (attr.index(i) >= pos && attr.index(i) < parameterNames.length + pos) {
                parameterNames[attr.index(i) - pos] = attr.variableName(i);
            }
        }
        for (int i = 0; i < ps.length; i++) {
            p = new LParameter();
            pname = parameterNames[i];
            if (pname == null) {
                p.setParameterName(ps[i].getName());
            } else {
                p.setParameterName(pname.equals("this") ? "t" : pname);
            }
            p.setParameterType(types[i]);
            p.setAnnotations(as[i]);
            p.setGeneralType(generalType[i]);
            p.setGeneral(generalType[i] != null);
            pss[i] = p;
        }
        return pss;
    }

    public static Class<?>[] getGeneralTypes(String typeStr) {
        int start = typeStr.indexOf("<");
        if (start != -1) {
            int last = typeStr.lastIndexOf(">");
            if (last > start) {
                String generalStr = typeStr.substring(start + 1, last);
                String[] types = generalStr.split(",");
                Class<?>[] classes = new Class[types.length];

                for (int i = 0; i < types.length; i++) {
                    try {
                        classes[i] = Class.forName(types[i].trim());
                    } catch (ClassNotFoundException e) {
                        throw new ConfigException("parse GeneralType" + types[i] + "error:" + e.getMessage());
                    }
                }
                return classes;
            }
        }
        return null;

    }


}




