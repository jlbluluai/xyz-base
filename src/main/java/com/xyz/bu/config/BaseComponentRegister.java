package com.xyz.bu.config;

import com.xyz.bu.cache.MyRedisTemplate;
import com.xyz.bu.lock.RequestLockHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Component注册中心
 *
 * @author xyz
 * @date 2020/9/21
 */
@Configuration
public class BaseComponentRegister {

    /**
     * Redis 模板
     */
    @Bean
    public MyRedisTemplate myRedisTemplate() {
        return new MyRedisTemplate();
    }

    /**
     * 请求锁
     */
    @Bean
    public RequestLockHandler requestLockHandler() {
        return new RequestLockHandler();
    }

}
