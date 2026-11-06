package net.risedata.register.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

import net.risedata.register.config.SystemConfigSelector;

/**
 * @Description : 启动系统操作 暂时只实现关闭
 * @ClassName EnableSystem
 * @Author lb
 * @Date 2021/12/22 17:46
 * @Version 1.0
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Import({SystemConfigSelector.class})
public @interface EnableSystem {}
