package com.xyz.bu.queue;

import java.util.List;

/**
 * @author xyz
 * @date 2020-08-05
 */
public interface QueueRunner {

    /**
     * 加入一个元素
     *
     * @param o
     */
    void add(Object o);

    /**
     * 加入一组元素
     *
     * @param objects
     */
    void batchAdd(List<?> objects);
}
