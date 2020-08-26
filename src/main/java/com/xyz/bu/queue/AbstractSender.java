package com.xyz.bu.queue;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * 发送者
 *
 * @author xyz
 * @date 2020-08-04
 */
public abstract class AbstractSender extends AbstractQueue implements QueueRunner {

    @Override
    public void add(Object o) {
        if (Objects.isNull(o)) {
            return;
        }

        myRedisTemplate.rpush(CACHE_KEY, JSON.toJSONString(o));
    }

    @Override
    public void batchAdd(List<?> objects) {
        if (CollectionUtils.isEmpty(objects)) {
            return;
        }

        String[] values = objects.stream().map(JSON::toJSONString).toArray(String[]::new);
        myRedisTemplate.rpushBatch(CACHE_KEY, values);
    }

}
