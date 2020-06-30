package net.lamgc.utils.encrypt;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Hmac加密工具类
 * <p>通过该类可以很便捷的进行Hmac加密操作.</p>
 */
public final class HmacEncryptUtils {

    private HmacEncryptUtils() {}

    /**
     * 对数据取Hmac数据摘要
     * @param data 数据
     * @param key 适用于Hmac加密的SecretKey对象
     * @param algorithm Hmac算法, 详见{@linkplain Algorithm Algorithm枚举类}
     * @return 返回摘要结果
     * @throws InvalidKeyException 当密钥无效或不适用于该加密时抛出
     */
    public static byte[] encrypt(byte[] data, SecretKey key, Algorithm algorithm) throws InvalidKeyException {
        Objects.requireNonNull(data);
        Objects.requireNonNull(key);
        Objects.requireNonNull(algorithm);
        try {
            Mac mac = Mac.getInstance(algorithm.algorithmName);
            mac.init(key);
            return mac.doFinal(data);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成指定算法的SecretKey对象
     * @param keyRules 密钥规则(例如接口的SecKey)
     * @param keySize 密钥长度
     * @param algorithm 密钥所属算法
     * @return 密钥对象
     */
    public static SecretKey getSecretKey(byte[] keyRules, int keySize, Algorithm algorithm) {
        return EncryptUtils.getSecretKey(keyRules, keySize, algorithm.algorithmName);
    }

    public enum Algorithm {
        HmacMD5("HmacMD5"),
        HmacSHA1("HmacSHA1"),
        HmacSHA224("HmacSHA224"),
        HmacSHA256("HmacSHA256"),
        HmacSHA384("HmacSHA384"),
        HmacSHA512("HmacSHA512"),
        ;

        public final String algorithmName;

        Algorithm(String algorithmName) {
            this.algorithmName = algorithmName;
        }
    }


}
