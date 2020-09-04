package com.xyz.bu.web;

import com.xyz.bu.queue.QueueExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author xyz
 * @date 2020/9/4
 */
@RestController
@RequestMapping(value = "/queue")
public class QueueController {

    @Autowired(required = false)
    @Qualifier("queueExecutor")
    private ThreadPoolTaskExecutor queueExecutor;

    @Autowired(required = false)
    private List<QueueExecutor> queues;

    /**
     * 查询队列健康情况
     */
    @GetMapping(value = "/condition")
    public Map<String, Object> queryQueueCondition() {
        if (Objects.isNull(queues)) {
            queues = Collections.emptyList();
        }
        if(Objects.isNull(queueExecutor)){
            queueExecutor = new ThreadPoolTaskExecutor();
        }

        Map<String, Object> result = new HashMap<>();

        result.put("队列总数", queues.size());
        result.put("现存活跃队列数", queueExecutor.getActiveCount());

        Map<String, Object> detail = new HashMap<>();
        queues.forEach(item -> detail.put("队列：" + item.queueName(),
                "存活与否：" + item.isActiveFlag() + " 队列大小：" + item.queueSize()));
        result.put("队列详情", detail);

        return result;
    }

}
