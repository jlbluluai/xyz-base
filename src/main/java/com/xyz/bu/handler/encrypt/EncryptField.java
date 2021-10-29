package com.xyz.bu.handler.encrypt;

import java.lang.annotation.*;

/**
 * 加密/解密字段
 * <p>
 * 只对一级字段并且是String类型生效
 *
 * @author xyz
 * @date 2021/10/25
 **/
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EncryptField {
}
