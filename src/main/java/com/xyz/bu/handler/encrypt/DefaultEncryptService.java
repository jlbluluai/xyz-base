package com.xyz.bu.handler.encrypt;

import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认加密/解密实现
 * <p>
 * 基于RSA实现
 * <p>
 * 请务必准备好公钥和私钥
 *
 * @author xyz
 * @date 2021/10/27
 **/
public class DefaultEncryptService extends AbstractEncryptService<DefaultEncryptService.DefaultEncryptAttribute> {

    public DefaultEncryptService(Map<String, String> attribute) {
        super(attribute);
    }

    @Override
    public String encrypt(String content) throws Exception {
        byte[] decoded = Base64.decodeBase64(attribute.getPublicKey());
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.encodeBase64String(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public String decrypt(String content) throws Exception {
        byte[] inputByte = Base64.decodeBase64(content.getBytes(StandardCharsets.UTF_8));
        byte[] decoded = Base64.decodeBase64(attribute.getPrivateKey());
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        return new String(cipher.doFinal(inputByte));
    }

    /**
     * 生成公钥、私钥
     *
     * @return 0:公钥 1:私钥
     */
    public static Map<Integer, String> genKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024, new SecureRandom());
        KeyPair keyPair = keyPairGen.generateKeyPair();

        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));

        Map<Integer, String> keyMap = new HashMap<>(2);
        keyMap.put(0, publicKeyString);
        keyMap.put(1, privateKeyString);
        return keyMap;
    }

    @Data
    static
    class DefaultEncryptAttribute {

        /**
         * 用作加密的公钥
         * <p>
         * 请注意此处公钥与下面私钥并不一定是一对，只是代表加密时用的秘钥
         */
        private String publicKey;

        /**
         * 用作解密的私钥
         * <p>
         * 请注意此处私钥与上面公钥并不一定是一对，只是代表解密时用的秘钥
         */
        private String privateKey;

    }

}
