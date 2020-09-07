package com.xyz.bu.lock;

import com.xyz.bu.cache.MyRedisTemplate;
import com.xyz.bu.utils.BizAssert;
import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author xyz
 * @date 2020/8/28
 */
@Aspect
@Component
public class RequestLockHandler {

    @Resource
    private MyRedisTemplate myRedisTemplate;

    @Pointcut(value = "@annotation(RequestLock)")
    public void requestLockPointcut() {
    }

    @Before(value = "requestLockPointcut()")
    public void doBefore(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        Annotation[][] parameterAnnotations = signature.getMethod().getParameterAnnotations();

        Long id = null;
        long lockTime = 0;
        TimeUnit timeUnit = null;
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            int index = ArrayUtils.indexOf(parameterAnnotations, parameterAnnotation);
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof RequestId) {
                    Object value = args[index];
                    lockTime = ((RequestId) annotation).time();
                    timeUnit = ((RequestId) annotation).unit();
                    if (lockTime <= 0) {
                        lockTime = 1;
                    }

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

        // 未获取到id值 不予以处理
        if (Objects.isNull(id)) {
            return;
        }

        // 判断是否锁住
        String cacheKey = signature.toString().split(" ")[1] + "." + id;
        boolean lock = myRedisTemplate.setexnx(cacheKey, "1", lockTime, timeUnit);
        BizAssert.isTrue(lock, "操作频繁，请稍后再试");
    }

}
