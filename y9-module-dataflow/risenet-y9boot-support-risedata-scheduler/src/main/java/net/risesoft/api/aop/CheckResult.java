package net.risesoft.api.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Indexed;
/**
 * 有需要被检查的对象加此方法 BindingResult 参数必须是接收的第二个
 * @author libo
 *2021年6月24日
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface CheckResult {

}
