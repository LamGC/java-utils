package net.lamgc.utils.encrypt;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

public class RSAEncrypt {

    public static int DEFAULT_KEY_SIZE = 3072;

	/**
	 * 生成一对RSA密钥
	 * @param keySize 密钥长度
	 * @return 密钥对
	 */
	public static KeyPair getKeyPair(int keySize){
        KeyPairGenerator keyPairGen;
		try{
            keyPairGen = KeyPairGenerator.getInstance("RSA");
        }catch(NoSuchAlgorithmException e){
		    throw new RuntimeException(e);
        }

        keyPairGen.initialize(keySize, new SecureRandom());

		return keyPairGen.generateKeyPair();
	}

    /**
     * 将符合X.509格式的数据密钥转换成RSAPublicKey对象
     * @param keyBytes 密钥数据
     * @return 通过密钥数据转换
     * @throws InvalidKeySpecException 当密钥数据无效时抛出
     */
	public static RSAPublicKey bytesToRSAPublicKey(byte[] keyBytes) throws InvalidKeySpecException {
        try {
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 将符合PKCS#8格式的密钥数据转换成RSAPrivateKey对象
     * @param keyBytes 密钥数据
     * @return 返回密钥数据对应RSAPrivateKey
     * @throws InvalidKeySpecException 当密钥数据无效时抛出
     */
	public static RSAPrivateKey bytesToRSAPrivateKey(byte[] keyBytes) throws InvalidKeySpecException {
        try {
            return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

	/** 
     * 公钥加密
     * @param publicKey 公钥 
     * @param plainTextData 明文数据 
     * @return 加密后的密文数据
     * @throws NoSuchPaddingException 不支持的填充方式
     * @throws InvalidKeyException 无效密钥
     * @throws BadPaddingException 密文损坏
     * @throws IllegalBlockSizeException 密文长度错误
     */ 
    public static byte[] encrypt(RSAPublicKey publicKey, byte[] plainTextData) throws NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Objects.requireNonNull(publicKey);
        byte[] output;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);  
            output = cipher.doFinal(plainTextData);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return output;  
    }
   
    /** 
     * 私钥加密
     * @param privateKey 私钥 
     * @param plainTextData 明文数据 
     * @return 加密后的密文数据
     * @throws NoSuchPaddingException 不支持的填充方式
     * @throws InvalidKeyException 无效密钥
     * @throws BadPaddingException 密文损坏
     * @throws IllegalBlockSizeException 密文长度错误
     */ 
    public static byte[] encrypt(RSAPrivateKey privateKey, byte[] plainTextData) throws NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {  
        Objects.requireNonNull(privateKey);
        byte[] output;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);  
            output = cipher.doFinal(plainTextData);  
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return output;
    }
   
    /** 
     * 公钥/私钥解密过程 
     *  
     * @param privateKey 私钥
     * @param cipherData  密文数据 
     * @return 解密后的明文数据
     * @throws NoSuchPaddingException 不支持的填充方式
     * @throws InvalidKeyException 无效密钥
     * @throws BadPaddingException 密文损坏
     * @throws IllegalBlockSizeException 密文长度错误
     */ 
    public static byte[] decrypt(RSAPrivateKey privateKey, byte[] cipherData) throws NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Objects.requireNonNull(privateKey);
        byte[] output;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            output = cipher.doFinal(cipherData);  
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return output; 
    }
   
    /** 
     * 公钥解密过程 
     *  
     * @param publicKey 公钥
     * @param cipherData 密文数据
     * @return 明解密后的明文数据
     * @throws NoSuchPaddingException 不支持的填充方式
     * @throws InvalidKeyException 无效密钥
     * @throws BadPaddingException 密文损坏
     * @throws IllegalBlockSizeException 密文长度错误
     */ 
    public static byte[] decrypt(RSAPublicKey publicKey, byte[] cipherData) throws NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
        Objects.requireNonNull(publicKey);
        byte[] output;
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);  
            output = cipher.doFinal(cipherData);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return output;
    }

}
