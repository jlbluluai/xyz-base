package com.xyz.bu.queue;

import com.xyz.bu.cache.MyRedisTemplate;

import javax.annotation.Resource;

/**
 * @author xyz
 * @date 2020-08-04
 */
public abstract class AbstractQueue {

    @Resource
    protected MyRedisTemplate myRedisTemplate;


    protected final String CACHE_KEY = setCacheKey();

    /**
     * 获取子类类的简单名
     *
     * @return
     */
    public abstract String setCacheKey();

}
