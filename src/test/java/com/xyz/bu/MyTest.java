package com.xyz.bu;

import com.xyz.bu.lock.Demo;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * @author xyz
 * @date 2020/8/28
 */
public class MyTest extends BaseTest{

    @Resource
    private Demo demo;

    @Test
    public void test(){
        demo.sys(1);
    }

}
