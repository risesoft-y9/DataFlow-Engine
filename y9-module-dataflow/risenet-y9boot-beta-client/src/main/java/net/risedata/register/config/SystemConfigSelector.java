package net.risedata.register.config;


import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @Description : 引入系统配置
 * @ClassName SystemConfigSelector
 * @Author lb
 * @Date 2021/12/22 17:51
 * @Version 1.0
 */
public class SystemConfigSelector implements ImportSelector {
    public static Class<?> rootClass;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        try {
            rootClass = Class.forName(importingClassMetadata.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new String[]{SystemConfig.class.getName()};
    }


}