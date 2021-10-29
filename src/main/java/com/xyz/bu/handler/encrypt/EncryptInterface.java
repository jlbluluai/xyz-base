package com.xyz.bu.handler.encrypt;

/**
 * 加密/解密接口
 *
 * @author xyz
 * @date 2021/10/27
 **/
public interface EncryptInterface {

    /**
     * 加密
     *
     * @param content 需要加密的内容
     */
    String encrypt(String content) throws Exception;

    /**
     * 解密
     *
     * @param content 需要解密的内容
     */
    String decrypt(String content) throws Exception;
}
