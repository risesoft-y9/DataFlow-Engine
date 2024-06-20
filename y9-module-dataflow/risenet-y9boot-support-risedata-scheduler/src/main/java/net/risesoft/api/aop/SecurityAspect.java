package net.risesoft.api.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import net.risesoft.pojo.Y9Result;

@Aspect
@Component
public class SecurityAspect {

    @Pointcut("@annotation(net.risesoft.api.aop.CheckResult)")
    public void doCheck() {
    }

    @Around("doCheck()")
    public Object doCheckResult(ProceedingJoinPoint joinPoint) throws Throwable {
        BindingResult result = (BindingResult) joinPoint.getArgs()[1];
        if (result.hasErrors()) {
            return Y9Result.failure(500,result.getFieldError().getDefaultMessage());
        }
        return joinPoint.proceed();
    }

    public static Y9Result<String> NO_SECURITY = Y9Result.failure(403, "no security");

}
