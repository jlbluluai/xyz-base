package com.xyz.bu.utils;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 强转工具类
 *
 * @author xyz
 * @date 2020/9/8
 */
public class CastUtils {

    private CastUtils() {

    }

    /**
     * 将Object转成List 前提Object是List型 否则返回空列表
     *
     * @param obj   Object 对象
     * @param clazz List中对象类型
     * @return 转换后的List
     */
    @NonNull
    public static <T> List<T> castList(@NonNull Object obj, @NonNull Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
        }
        return result;
    }

}
