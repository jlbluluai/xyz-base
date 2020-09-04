package com.xyz.bu.queue;

/**
 * @author xyz
 * @date 2020-08-05
 */
public interface QueueExecutor {

    /**
     * 执行队列
     */
    void executeQueue();

    /**
     * 队列活跃标志
     *
     * @return true/false
     */
    boolean isActiveFlag();

    /**
     * 获取队列名称
     *
     * @return 队列名称
     */
    String queueName();

    /**
     * 获取队列大小
     *
     * @return 队列大小
     */
    long queueSize();

}
