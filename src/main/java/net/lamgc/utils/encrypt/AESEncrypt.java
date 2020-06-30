package net.lamgc.utils.encrypt;

import javax.crypto.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * AES对称加密工具类
 * @author LamGC
 */
public final class AESEncrypt {

    private static final String Algorithm = "AES";

    private AESEncrypt() {}

    /**
     * 根据密钥规则(可以是密钥)生成一个AES密钥对象
     * @param encodeRules 密钥规则,可以填入原始密码
     * @param keySize 随机源大小,默认128
     * @return AES密钥
     */
    public static SecretKey getSecretKey(String encodeRules, int keySize){
        return getSecretKey(encodeRules.getBytes(), keySize);
    }

    /**
     * 根据密钥规则(可以是密钥)生成一个AES密钥对象
     * @param encodeRules 密钥规则, 可以使用随机数据
     * @param keySize 随机源大小,默认128
     * @return AES密钥
     */
    public static SecretKey getSecretKey(byte[] encodeRules, int keySize){
        return EncryptUtils.getSecretKey(encodeRules, keySize, Algorithm);
    }

    static SecretKey BytesToSecretKey(byte[] keyEncode){
        return EncryptUtils.BytesToSecretKey(keyEncode, Algorithm);
    }

    /**
     * 加密数据.
     * @param data 待加密的数据
     * @param key AES密钥对象
     * @return 加密后的数据
     * @throws NoSuchPaddingException 不支持的填充方式
     * @throws InvalidKeyException 无效密钥异常
     * @throws BadPaddingException 错误填充异常
     * @throws IllegalBlockSizeException 数据块错误(可能是数据不完整导致的)
     */
    public static byte[] encrypt(byte[] data, SecretKey key) throws NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(Algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        //初始化为加密模式,导入密钥
        //初始化密码器
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //进行加密并返回数据
        return cipher.doFinal(data);
    }

    /**
     * 解密数据.
     * @param data 待解密的数据
     * @param key AES密钥对象
     * @return 解密后的数据
     * @throws NoSuchPaddingException 不支持的填充方式
     * @throws InvalidKeyException 无效密钥异常
     * @throws BadPaddingException 错误填充异常
     * @throws IllegalBlockSizeException 数据块错误(可能是数据不完整导致的)
     */
    public static byte[] decrypt(byte[] data, SecretKey key) throws NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(Algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        //初始化为加密模式,导入密钥
        //初始化密码器
        cipher.init(Cipher.DECRYPT_MODE, key);
        //进行加密并返回数据
        return cipher.doFinal(data);
    }

}



