package com.xyz.bu.common;

/**
 * @author xyz
 * @date 2019-06-03
 */
public class BaseConstant {

    public static final ThreadLocal<Long> START_TIME = new ThreadLocal<>();

    public static final int REDIS_KEY_NOT_EXIST = -2;
    public static final int REDIS_KEY_NOT_SET_EXPIRE = -1;

}
