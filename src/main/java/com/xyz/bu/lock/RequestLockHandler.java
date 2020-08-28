package com.xyz.bu.lock;

import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

/**
 * @author xyz
 * @date 2020/8/28
 */
@Aspect
@Component
public class RequestLockHandler {

    @Pointcut(value = "@annotation(RequestLock)")
    public void requestLockPointcut() {
    }

    @Before(value = "requestLockPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();

        Long id = null;
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            int index = ArrayUtils.indexOf(parameterAnnotations, parameterAnnotation);
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof RequestId) {
                    Object value = args[index];

                    if (value instanceof Long) {
                        id = (Long) value;
                        break;
                    }
                    if (value instanceof Integer) {
                        id = Long.valueOf((Integer) value);
                        break;
                    }
                }
            }
        }

    }


}
