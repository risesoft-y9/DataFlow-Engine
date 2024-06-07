package net.risesoft.api.aop;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * 检查查询 条件
 * @author libo
 *2021年6月24日
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Indexed
public @interface CheckHttpForArgs {

}
