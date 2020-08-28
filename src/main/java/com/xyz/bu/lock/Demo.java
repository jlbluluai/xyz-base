package com.xyz.bu.lock;

import org.springframework.stereotype.Service;

/**
 * @author Zhu WeiJie
 * @date 2020/8/28
 */
@Service
public class Demo {

    @RequestLock
    public void sys(@RequestId int uid){

    }
}
