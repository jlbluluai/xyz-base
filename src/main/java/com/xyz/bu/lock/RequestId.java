package com.xyz.bu.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author xyz
 * @date 2020/8/28
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestId {

    long time() default 1;

    TimeUnit unit() default TimeUnit.SECONDS;

}
