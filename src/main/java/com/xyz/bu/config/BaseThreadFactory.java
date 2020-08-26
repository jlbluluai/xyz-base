package com.xyz.bu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @author xyz
 * @date 2019-11-17
 */
@Configuration
public class BaseThreadFactory {

    @Bean("queueExecutor")
    public ThreadPoolTaskExecutor queueExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(8);
        executor.setKeepAliveSeconds(60);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("queueService-");
        return executor;
    }

}
