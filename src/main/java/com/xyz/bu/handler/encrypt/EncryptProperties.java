package com.xyz.bu.handler.encrypt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 加密/解密的属性
 *
 * @author xyz
 * @date 2021/10/27
 **/
@ConfigurationProperties(prefix = "xyz.base.encrypt")
@Data
public class EncryptProperties {

    /**
     * 是否启用，默认不启用
     */
    private boolean enable;

    /**
     * 使用的算法，默认无需配置，若自己去实现，配置加密算法的全限定路径，但请标准实现EncryptInterface接口
     */
    private String algorithm;

    /**
     * 配合算法的属性集合
     */
    private Map<String, String> attribute;

}
