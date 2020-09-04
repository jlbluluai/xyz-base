package com.xyz.bu.queue;

import com.alibaba.fastjson.JSON;
import com.xyz.bu.exception.BusinessException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 接收者
 *
 * @author xyz
 * @date 2020-08-04
 */
public abstract class AbstractReceiver<T> extends AbstractQueue implements QueueExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractReceiver.class);

    @Autowired(required = false)
    @Qualifier("queueExecutor")
    private ThreadPoolTaskExecutor executor;

    private final Class<T> handleClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    private final int readSize = readSize();

    /**
     * 存活标志
     */
    private boolean activeFlag;

    @PostConstruct
    public void init() {
        if (Objects.isNull(executor)) {
            throw new BusinessException("未开启队列，请不要在程序里注册队列的bean，请核对");
        }
    }

    @Override
    public void executeQueue() {
        executor.execute(() -> {
            try {
                activeFlag = true;
                while (true) {
                    List<T> dataList = getBatch();
                    if (CollectionUtils.isNotEmpty(dataList)) {
                        handle(dataList);
                    }

                    try {
                        Thread.sleep(500);
                    } catch (Exception e) {
                        LOGGER.error("thread sleep error", e);
                    }
                }
            } catch (Exception e) {
                activeFlag = false;
                LOGGER.error("queue ex and quit queue={}", CACHE_KEY, e);
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

    @Override
    public boolean isActiveFlag() {
        return activeFlag;
    }

    @Override
    public String queueName() {
        return CACHE_KEY;
    }

    @Override
    public long queueSize() {
        return myRedisTemplate.lsize(CACHE_KEY);
    }

}
