package net.risedata.jdbc.annotations.order;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 降序排序
 * @author libo
 *2020年10月15日
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(Descs.class)
public @interface Desc {
	/**
	 * 排序级别 越小越优先
	 * @return
	 */
    int leve() default 0;
    
    /**
     * 表达式什么情况下生效
     * @return
     */
    String expression() default "";
    
    /**
     * 根据表达式返回级别 (未实现)
     */
    String leveExpression() default "";
}
