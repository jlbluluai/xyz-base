package com.xyz.bu.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author xyz
 * @date 2020/9/4
 */
@RestController
@RequestMapping(value = "/threadPool")
public class ThreadPoolController {

    @Autowired(required = false)
    private List<ThreadPoolTaskExecutor> executors;

    @PostConstruct
    public void init() {
        if (Objects.isNull(executors)) {
            executors = Collections.emptyList();
        }
    }


    /**
     * 查询线程池健康情况
     */
    @GetMapping(value = "/condition")
    public Map<String, Object> queryQueueCondition() {
        Map<String, Object> result = new HashMap<>();

        result.put("池总数", executors.size());

        Map<String, Object> detail = new HashMap<>();
        executors.forEach(item -> detail.put("池：" + item.getThreadNamePrefix(),
                String.format("池核心线程数：%s，池当前活跃线程数：%s，池等待队列剩余大小：%s，池等待队列当前大小：%s",
                        item.getCorePoolSize(), item.getActiveCount(),
                        item.getThreadPoolExecutor().getQueue().remainingCapacity(), item.getThreadPoolExecutor().getQueue().size())));
        result.put("池详情", detail);

        return result;
    }

}
