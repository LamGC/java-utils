package net.lamgc.utils.encrypt;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

public final class EncryptUtils {

    /**
     * 字节数组转SecretKey
     * @param keyEncode 密钥编码数据
     * @param algorithm 密钥所属算法
     * @return SecretKey对象
     */
    public static SecretKey BytesToSecretKey(byte[] keyEncode, String algorithm){
        Objects.requireNonNull(keyEncode);
        Objects.requireNonNull(algorithm);
        return new SecretKeySpec(keyEncode, algorithm);
    }

    /**
     * 根据密钥规则(可以是密钥)生成一个AES密钥对象
     * @param encodeRules 密钥规则, 可以使用随机数据
     * @param keySize 随机源大小,默认128
     * @return AES密钥
     */
    public static SecretKey getSecretKey(byte[] encodeRules, int keySize, String keyAlgorithm){
        if(keySize <= 0){
            //设置一个初始值
            keySize = 128;
        }
        KeyGenerator keygen;
        try{
            keygen = KeyGenerator.getInstance(keyAlgorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        keygen.init(keySize, new SecureRandom(encodeRules));
        return BytesToSecretKey(keygen.generateKey().getEncoded(), keyAlgorithm);
    }

}
