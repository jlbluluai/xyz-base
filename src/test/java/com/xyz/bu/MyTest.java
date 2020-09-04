package com.xyz.bu;

import com.xyz.bu.lock.Demo;
import org.junit.Test;

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
    private Demo demo;

    @Test
    public void test(){
        demo.sys(1);
    }

}
