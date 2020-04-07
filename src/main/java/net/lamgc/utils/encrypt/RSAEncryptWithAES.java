package net.lamgc.utils.encrypt;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

/**
 * RSA与AES联合加密
 * 原理与GPG类似, 通过AES加密数据, 通过RSA加密AES密钥来绕开RSA无法加密过大数据的问题.
 *
 * 加密数据结构:
 * <pre>
 *     keyLength(4bytes), keyData(keyLength), encryptData(?)
 * </pre>
 * 通过获取keyLength来截出keyData, 通过RSA解密keyData获得AES密钥, 然后通过AES密钥解密encryptData即可,
 * 当获取了keyData后, 剩下的数据就是encryptData了.
 */
public final class RSAEncryptWithAES {

    private RSAEncryptWithAES() {}

    /**
     * 加密数据
     * @param data 原始数据
     * @param privateKey RSA私钥
     * @return 加密后的数据
     * @throws NoSuchPaddingException 不支持的填充方式
     * @throws InvalidKeyException 无效密钥异常
     * @throws BadPaddingException 错误填充异常
     * @throws IllegalBlockSizeException 数据块错误(可能是数据不完整导致的)
     */
    public static byte[] encrypt(byte[] data, RSAPrivateKey privateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        ByteBuffer buf = ByteBuffer.allocate(data.length + 2048);
        byte[] keyRules = new byte[512];
        new SecureRandom().nextBytes(keyRules);
        SecretKey aesKey = AESEncrypt.getSecretKey(keyRules, 256);
        byte[] keyData = RSAEncrypt.encrypt(privateKey, aesKey.getEncoded());
        buf.putInt(keyData.length);
        buf.put(keyData);
        buf.put(AESEncrypt.encrypt(data, aesKey));
        byte[] arr = buf.array();
        return Arrays.copyOf(arr, buf.position());
    }

    /**
     * 加密数据
     * @param data 原始数据
     * @param publicKey RSA私钥
     * @return 加密后的数据
     * @throws NoSuchPaddingException 不支持的填充方式
     * @throws InvalidKeyException 无效密钥异常
     * @throws BadPaddingException 错误填充异常
     * @throws IllegalBlockSizeException 数据块错误(可能是数据不完整导致的)
     */
    public static byte[] encrypt(byte[] data, RSAPublicKey publicKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        ByteBuffer buf = ByteBuffer.allocate(data.length + 2048);
        byte[] keyRules = new byte[512];
        new SecureRandom().nextBytes(keyRules);
        SecretKey aesKey = AESEncrypt.getSecretKey(keyRules, 256);
        byte[] keyData = RSAEncrypt.encrypt(publicKey, aesKey.getEncoded());
        buf.putInt(keyData.length);
        buf.put(keyData);
        buf.put(AESEncrypt.encrypt(data, aesKey));
        byte[] arr = buf.array();
        return Arrays.copyOf(arr, buf.position());
    }

    /**
     * 解密数据
     * @param data 加密数据
     * @param publicKey RSA公钥
     * @return 解密后的数据
     * @throws NoSuchPaddingException 不支持的填充方式
     * @throws InvalidKeyException 无效密钥异常
     * @throws BadPaddingException 错误填充异常
     * @throws IllegalBlockSizeException 数据块错误(可能是数据不完整导致的)
     */
    public static byte[] decrypt(byte[] data, RSAPublicKey publicKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        ByteBuffer buf = ByteBuffer.wrap(data);
        byte[] keyData = new byte[buf.getInt()];
        buf.get(keyData);
        SecretKey aesKey = AESEncrypt.BytesToSecretKey(RSAEncrypt.decrypt(publicKey, keyData));
        byte[] encryptData = new byte[buf.capacity() - buf.position()];
        buf.get(encryptData);
        return AESEncrypt.decrypt(encryptData, aesKey);
    }

    /**
     * 解密数据
     * @param data 加密数据
     * @param privateKey RSA公钥
     * @return 解密后的数据
     * @throws NoSuchPaddingException 不支持的填充方式
     * @throws InvalidKeyException 无效密钥异常
     * @throws BadPaddingException 错误填充异常
     * @throws IllegalBlockSizeException 数据块错误(可能是数据不完整导致的)
     */
    public static byte[] decrypt(byte[] data, RSAPrivateKey privateKey) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        ByteBuffer buf = ByteBuffer.wrap(data);
        byte[] keyData = new byte[buf.getInt()];
        buf.get(keyData);
        SecretKey aesKey = AESEncrypt.BytesToSecretKey(RSAEncrypt.decrypt(privateKey, keyData));
        byte[] encryptData = new byte[buf.capacity() - buf.position()];
        buf.get(encryptData);
        return AESEncrypt.decrypt(encryptData, aesKey);
    }

}
