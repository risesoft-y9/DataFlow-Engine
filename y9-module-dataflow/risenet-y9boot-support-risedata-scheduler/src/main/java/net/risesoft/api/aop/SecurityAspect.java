package net.risesoft.api.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import net.risesoft.api.persistence.model.GetEnvironment;
import net.risesoft.api.persistence.model.GetService;
import net.risesoft.api.security.ConcurrentSecurity;
import net.risesoft.api.security.SecurityManager;
import net.risesoft.pojo.Y9Result;

import java.util.HashMap;
import java.util.Map;


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

//    @Pointcut("@annotation(net.risesoft.api.aop.CheckHttpForArgs)")
//    public void checkHttpForArgs() {
//    }

    @Autowired
    SecurityManager securityManager;

    public static Y9Result NO_SECURITY = Y9Result.failure(403, "no security");

    private Map<Class<?>, Byte> skipMap = new HashMap<>();



}
