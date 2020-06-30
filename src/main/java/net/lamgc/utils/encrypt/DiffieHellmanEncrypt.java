package net.lamgc.utils.encrypt;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * DH密钥交换
 */
public final class DiffieHellmanEncrypt {

    static {
        System.setProperty("jdk.crypto.KeyAgreement.legacyKDF", "true");
    }

    /**
     * 非对称加密密钥算法
     */
    private static final String KEY_ALGORITHM = "DH";

    public static final int DEFAULT_KEY_SIZE = 512;

    private KeyPair keyPair;

    /**
     * 使用默认的密钥长度构造一个DH密钥交换对象
     * @see #DEFAULT_KEY_SIZE
     */
    public DiffieHellmanEncrypt(){
        initKey(DEFAULT_KEY_SIZE);
    }

    /**
     * 构造一个DH密钥交换对象并指定DH密钥长度
     * @param keySize 密钥长度
     */
    public DiffieHellmanEncrypt(int keySize){
        initKey(keySize);
    }

    /**
     * 初始化己方密钥
     * @param keySize 密钥长度
     */
    public void initKey(int keySize){
        //实例化密钥对生成器
        KeyPairGenerator keyPairGenerator;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        //初始化密钥对生成器
        keyPairGenerator.initialize(keySize);
        //生成密钥对
        this.keyPair = keyPairGenerator.generateKeyPair();
    }

    /**
     * 获取己方公钥
     * @return 己方公钥对象
     */
    public PublicKey getPublicKey(){
        return this.keyPair.getPublic();
    }

    /**
     * 获得DH公共密钥
     * @param publicKey 对方公钥
     * @param algorithm 公共密钥所对应的加密算法
     * @return DH公共密钥
     * @throws InvalidKeyException 当对方公钥数据无效时抛出
     */
    public SecretKey getSecretKey(byte[] publicKey, Algorithm algorithm) throws InvalidKeyException {
        try{
            KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
            PublicKey pubKey = keyFactory.generatePublic(x509KeySpec);
            return getSecretKey(pubKey, algorithm);
        }catch(NoSuchAlgorithmException | InvalidKeySpecException e){
            throw new IllegalStateException(e);
        }
    }

    /**
     * 引入对方公钥, 构造指定算法的密钥
     * @param publicKey 对方公钥
     * @param algorithm 公共密钥算法
     * @return 密钥对象
     * @throws InvalidKeyException 当PublicKey无效时抛出
     */
    public SecretKey getSecretKey(PublicKey publicKey, Algorithm algorithm) throws InvalidKeyException {
        try{
            KeyAgreement keyAgree = KeyAgreement.getInstance(KEY_ALGORITHM);
            keyAgree.init(this.keyPair.getPrivate());
            keyAgree.doPhase(publicKey, true);
            return keyAgree.generateSecret(algorithm.algorithmName);
        }catch(NoSuchAlgorithmException e){
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("unused")
    public enum Algorithm{
        AES("AES"),
        RC2("RC2"),
        RC4("ARCFOUR"),
        Blowfish("Blowfish"),
        DES("DES"),
        DES_ede("DESede"),
        HmacMD5("HmacMD5"),
        HmacSHA1("HmacSHA1"),
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
