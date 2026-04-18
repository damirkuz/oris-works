package ru.kuzdikenov.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class LoggingAspect {
//    @Pointcut("execution(* ru.kuzdikenov..*.*(..)) && !within(ru.kuzdikenov.dto..*) && !within(ru.kuzdikenov.config..*)")
//    public void logExecution() {
//    }

    @Pointcut("@annotation(Loggable)")
    public void logAnnotated() {
    }

    @Around("logAnnotated()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Entering log execution");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        log.info("Start execution {}.{}", className, methodName);
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        log.info("Finish executing {}.{}", className, methodName);
        return result;
    }
}
