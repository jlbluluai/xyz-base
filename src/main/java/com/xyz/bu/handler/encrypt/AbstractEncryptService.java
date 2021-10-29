package com.xyz.bu.handler.encrypt;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

/**
 * 加密/解密抽象
 * <p>
 * 提供一般实现的公用方案
 *
 * @author xyz
 * @date 2021/10/27
 **/
public abstract class AbstractEncryptService<T> implements EncryptInterface {

    private final Class<T> attributeClazz = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    protected T attribute;

    public AbstractEncryptService(Map<String, String> attribute) {
        String jsonString = JSON.toJSONString(attribute);
        this.attribute = JSON.parseObject(jsonString, attributeClazz);
    }

}
