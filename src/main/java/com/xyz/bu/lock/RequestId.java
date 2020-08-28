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
}
