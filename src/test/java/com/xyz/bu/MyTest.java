package com.xyz.bu;

import com.xyz.bu.cache.MyRedisTemplate;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;

/**
 * @author xyz
 * @date 2020/8/28
 */
public class MyTest extends BaseTest{

    @Resource(name = "queueExecutor")
    private ThreadPoolTaskExecutor executor;

    @Test
    public void test1() throws InterruptedException {
        System.out.println(executor.getCorePoolSize());
        System.out.println(executor.getActiveCount());

        executor.execute(()->{
            try {
                Thread.sleep(50000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        executor.execute(()->{
            System.out.println(" ");
        });

        Thread.sleep(5000);
        System.out.println(executor.getActiveCount());

        Thread.sleep(100000);
    }

    @Resource
    private MyRedisTemplate myRedisTemplate;

    @Test
    public void testRedis(){
        myRedisTemplate.get("a");
    }

}
