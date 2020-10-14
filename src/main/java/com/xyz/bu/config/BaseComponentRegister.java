package com.xyz.bu.config;

import com.xyz.bu.cache.MyRedisTemplate;
import com.xyz.bu.es.ElasticsearchTemplate;
import com.xyz.bu.lock.RequestLockHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

    private static final String ELASTICSEARCH_ENABLE = "spring.elasticsearch.enable";

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

    /**
     * Elasticsearch模板
     * <p>
     * 默认不开启
     */
    @ConditionalOnProperty(value = ELASTICSEARCH_ENABLE, havingValue = "true")
    @Bean
    public ElasticsearchTemplate elasticsearchTemplate() {
        return new ElasticsearchTemplate();
    }

}
