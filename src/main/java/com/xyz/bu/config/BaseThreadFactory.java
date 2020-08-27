package com.xyz.bu.config;

import com.xyz.bu.exception.BusinessException;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 基础的线程池工厂
 *
 * @author xyz
 * @date 2019-11-17
 */
@Configuration
public class BaseThreadFactory implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseThreadFactory.class);

    private static final String QUEUE_EXECUTOR_ENABLE = "queue.executor.enable";
    private static final String QUEUE_EXECUTOR_SIZE = "queue.executor.size";
    private int queueExecutorSize = 4;

    @Resource
    private Environment environment;

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("BaseThreadFactory set properties");

        // 获取队列线程池大小 即队列数量
        if("true".equals(environment.getProperty(QUEUE_EXECUTOR_ENABLE))){
            if (environment.containsProperty(QUEUE_EXECUTOR_SIZE)) {
                int size = NumberUtils.toInt(environment.getProperty(QUEUE_EXECUTOR_SIZE));
                if (size > queueExecutorSize) {
                    queueExecutorSize = size;
                }
            }
        }
    }

    @ConditionalOnProperty(value = QUEUE_EXECUTOR_ENABLE, havingValue = "true")
    @Bean("queueExecutor")
    public ThreadPoolTaskExecutor queueExecutor() {
        LOGGER.info("build queueExecutor, it's size = {}", queueExecutorSize);

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(queueExecutorSize);
        executor.setMaxPoolSize(queueExecutorSize);
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler(new QueueExecutorPolicy());
        executor.setThreadNamePrefix("queueExecutor-");

        return executor;
    }

    /**
     * 队列线程池的拒绝策略
     */
    public static class QueueExecutorPolicy implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            throw new BusinessException("queue size is over the max of configuration, please check it");
        }

    }

}
