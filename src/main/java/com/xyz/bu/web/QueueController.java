package com.xyz.bu.web;

import com.xyz.bu.exception.BusinessException;
import com.xyz.bu.queue.QueueExecutor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
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

    @PostConstruct
    public void init() {
        if (Objects.isNull(queues)) {
            queues = Collections.emptyList();
        }
        if (Objects.isNull(queueExecutor)) {
            queueExecutor = new ThreadPoolTaskExecutor();
        }
    }

    /**
     * 查询队列健康情况
     */
    @GetMapping(value = "/condition")
    public Map<String, Object> queryQueueCondition() {
        Map<String, Object> result = new HashMap<>();

        result.put("队列总数", queues.size());
        result.put("现存活跃队列数", queueExecutor.getActiveCount());

        Map<String, Object> detail = new HashMap<>();
        queues.forEach(item -> detail.put("队列：" + item.queueName(),
                "存活与否：" + item.isActiveFlag() + " 队列大小：" + item.queueSize()));
        result.put("队列详情", detail);

        return result;
    }

    /**
     * 重启一个队列 若该队列非活
     */
    @GetMapping("/restart/{queueName}")
    public void restartOneQueue(@PathVariable String queueName) {
        queues.forEach(item -> {
            if (StringUtils.equals(item.queueName(), queueName)) {
                if (item.isActiveFlag()) {
                    throw new BusinessException("队列尚且活跃，无需重启");
                }
                item.executeQueue();
            }
        });
    }

    /**
     * 重启所有队列 若队列非活
     */
    @GetMapping("/restartAll")
    public void restartAll() {
        queues.forEach(item -> {
            if (!item.isActiveFlag()) {
                item.executeQueue();
            }
        });
    }

}
