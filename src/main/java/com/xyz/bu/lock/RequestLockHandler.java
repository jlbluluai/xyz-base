package com.xyz.bu.lock;

import com.xyz.bu.cache.MyRedisTemplate;
import com.xyz.bu.common.BaseConstant;
import com.xyz.bu.exception.BusinessException;
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
        int lockTime = 0;
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            int index = ArrayUtils.indexOf(parameterAnnotations, parameterAnnotation);
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof RequestId) {
                    Object value = args[index];
                    lockTime = ((RequestId) annotation).time();
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
        if (myRedisTemplate.ttl(cacheKey) != BaseConstant.REDIS_KEY_NOT_EXIST) {
            throw new BusinessException("操作频繁，请稍后再试");
        }

        // 设置过期时间
        myRedisTemplate.setex(cacheKey, lockTime, "1");
    }

}
