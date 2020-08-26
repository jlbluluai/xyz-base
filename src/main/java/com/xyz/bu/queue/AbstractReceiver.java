package com.xyz.bu.queue;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * 接收者
 *
 * @author xyz
 * @date 2020-08-04
 */
public abstract class AbstractReceiver<T> extends AbstractQueue implements QueueExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReceiver.class);

    @Resource
    private Executor queueExecutor;

    private final Class<T> handleClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private final int readSize = readSize();

    @Override
    public void executeQueue() {
        queueExecutor.execute(() -> {
            while (true) {
                List<T> dataList = getBatch();
                if (CollectionUtils.isNotEmpty(dataList)) {
                    handle(dataList);
                }

                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    LOGGER.error("#AbstractReceiver.executeQueue thread sleep error", e);
                }
            }
        });
    }

    /**
     * 获取一组元素
     *
     * @return
     */
    private List<T> getBatch() {
        // 若大小为1 直接用pop方式
        if (readSize == 1) {
            String result = myRedisTemplate.lpop(CACHE_KEY);
            return Collections.singletonList(JSON.parseObject(result, handleClazz));
        }

        List<String> result = myRedisTemplate.lpopBatch(CACHE_KEY, readSize);
        return result.stream().map(item -> JSON.parseObject(item, handleClazz)).collect(Collectors.toList());
    }


    /**
     * 每次读取大小
     *
     * @return
     */
    protected abstract int readSize();

    /**
     * 处理方法 一般情况 具体的队列只需要关注该方法进行处理即可
     *
     * @param dataList
     */
    protected abstract void handle(List<T> dataList);


}
