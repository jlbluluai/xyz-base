package com.xyz.bu.lock;

import java.lang.annotation.*;

/**
 * @author xyz
 * @date 2020/8/28
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestId {

    /**
     * 锁住的时间
     *
     * @return 秒数
     */
    int time() default 1;

}
