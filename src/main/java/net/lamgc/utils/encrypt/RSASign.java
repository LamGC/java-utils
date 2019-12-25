package net.lamgc.utils.encrypt;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSASign {
    
    /** 
     * RSA签名 
     * @param content 待签名数据 
     * @param privateKey 私钥
     * @param algorithm 签名算法
     * @return 签名值 
     * @throws SignatureException 签名出错时抛出
     * @throws InvalidKeyException 无效密钥,当密钥错误或无效时抛出
     * @throws InvalidKeySpecException 密钥规范无效
    */ 
    public static byte[] sign(byte[] content, byte[] privateKey, SignAlgorithm algorithm) throws SignatureException, InvalidKeyException, InvalidKeySpecException{
        PKCS8EncodedKeySpec privatePKCS8 = new PKCS8EncodedKeySpec(privateKey);
        PrivateKey priKey = null;
        Signature signature = null;
        try {
            priKey = KeyFactory.getInstance("RSA").generatePrivate(privatePKCS8);
            signature = Signature.getInstance(algorithm.algorithm);
        }catch(NoSuchAlgorithmException ignored){
            //算法是固定的,应该不会抛出算法不支持异常
        }
        assert signature != null;
        signature.initSign(priKey);
        signature.update(content);
        return signature.sign();  
    }

    /**
     * RSA签名
     * @param content 待签名数据
     * @param privateKey RSA私钥对象
     * @param algorithm 签名算法
     * @return 签名值
     * @throws SignatureException 签名出错时抛出
     * @throws InvalidKeyException 无效密钥,当密钥错误或无效时抛出
     * @throws InvalidKeySpecException 密钥规范无效
     */
    public static byte[] sign(byte[] content, RSAPrivateKey privateKey, SignAlgorithm algorithm) throws InvalidKeySpecException, SignatureException, InvalidKeyException {
        return sign(content, privateKey.getEncoded(), algorithm);
    }

       
    ////////////////// 验签 //////////////////

    /**
     * RSA验签检查
     * @param content 待验签数据
     * @param sign 签名值
     * @param publicKey RSA公钥
     * @param algorithm 签名算法
     * @return 布尔值
     * @throws SignatureException 签名出错时抛出
     * @throws InvalidKeyException 无效密钥,当密钥错误或无效时抛出
     * @throws InvalidKeySpecException 密钥规范无效
     */
    public static boolean checkSign(byte[] content, byte[] sign, byte[] publicKey, SignAlgorithm algorithm) throws InvalidKeySpecException, InvalidKeyException, SignatureException{
        PublicKey pubKey = null;
        Signature signature = null;
        try {
            pubKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKey));
            signature = Signature.getInstance(algorithm.algorithm);
        }catch(NoSuchAlgorithmException ignored){
            //算法是固定的,应该不会抛出算法不支持异常
        }
        assert signature != null;
        signature.initVerify(pubKey);
        signature.update(content);
        return signature.verify(sign);
    }
    
    /**
     * RSA签名验证
     * @param content 待验证内容
     * @param sign 签名值
     * @param publicKey RSA公钥对象
     * @param algorithm 签名算法
     * @return 签名是否验证成功
     * @throws InvalidKeySpecException 密钥规范无效异常
     * @throws InvalidKeyException 密钥无效异常
     * @throws SignatureException 签名异常
     */
    public static boolean checkSign(byte[] content, byte[] sign, RSAPublicKey publicKey, SignAlgorithm algorithm) throws InvalidKeySpecException, InvalidKeyException, SignatureException {
        return checkSign(content, sign, publicKey.getEncoded(), algorithm);
    }



    /**
     * 限定了对于RSA合法算法的算法集
     */
    public enum SignAlgorithm{
        /**
         * Md5摘要签名(MD2withRSA)
         */
        MD2("MD2withRSA"),
        /**
         * Md5摘要签名(MD5withRSA)
         */
        MD5("MD5withRSA"),

        /**
         * SHA256摘要签名(SHA1withRSA)
         */
        SHA1("SHA1withRSA"),
        /**
         * SHA256摘要签名(SHA224withRSA)
         */
        SHA224("SHA224withRSA"),
        /**
         * SHA256摘要签名(SHA256withRSA)
         */
        SHA256("SHA256withRSA"),
        /**
         * SHA384摘要签名(SHA384withRSA)
         */
        SHA384("SHA384withRSA"),
        /**
         * SHA512摘要签名(SHA512withRSA)
         */
        SHA512("SHA512withRSA");
        /**
         * 指定的签名算法
         */
        final String algorithm;

        SignAlgorithm(String Algorithm){
            this.algorithm = Algorithm;
        }

    }



}
