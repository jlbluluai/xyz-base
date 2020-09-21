package com.xyz.bu.config;

import com.xyz.bu.web.QueueController;
import com.xyz.bu.web.ThreadPoolController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Controller注册中心
 *
 * @author xyz
 * @date 2020/9/21
 */
@Configuration
public class BaseControllerRegister {

    /**
     * 队列
     */
    @Bean
    public QueueController queueController() {
        return new QueueController();
    }

    /**
     * 线程池
     */
    @Bean
    public ThreadPoolController threadPoolController() {
        return new ThreadPoolController();
    }

}
