package net.lamgc.utils.encrypt;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Random;

/**
 * 对{@link RSAEncrypt}的测试
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RSAEncryptTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    /**
     * 测试中生成的临时密钥
     */
    private static KeyPair key;
    /**
     * 固定密钥长度
     */
    private static final int keySize = RSAEncrypt.DEFAULT_KEY_SIZE;
    /**
     * 设置原始数据为最大数据长度
     */
    private static byte[] rawData = new byte[keySize / 8 - 11];

    @Test
    public void A_GenerateKeyTest(){
        log.info("Generate key...[keySize: {}]", keySize);
        long time = new Date().getTime();
        key = RSAEncrypt.getKeyPair(keySize);
        log.info("Generate key done![time: {}ms]", new Date().getTime() - time);
    }

    @Test
    public void B_EncryptDataTest() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        log.info("---------- Encrypt: publicKey, Decrypt: privateKey ----------");
        new Random().nextBytes(rawData);
        log.info("Encrypt Data(publicKey)...[rawDataLength: {}B]", rawData.length);
        long time = new Date().getTime();
        byte[] encryptData = RSAEncrypt.encrypt((RSAPublicKey) key.getPublic(), rawData);
        log.info("Encrypt Data(publicKey) done![time: {}ms]", new Date().getTime() - time);
        log.info("Decrypt Data(privateKey)...[encryptDataLength: {}B]", encryptData.length);
        time = new Date().getTime();
        Assert.assertArrayEquals(rawData, RSAEncrypt.decrypt((RSAPrivateKey) key.getPrivate(), encryptData));
        log.info("Decrypt Data(privateKey) done![time: {}ms]", new Date().getTime() - time);

        log.info("---------- Encrypt: privateKey, Decrypt: publicKey ----------");
        new Random().nextBytes(rawData);
        log.info("Encrypt Data(privateKey)...[rawDataLength: {}B]", rawData.length);
        time = new Date().getTime();
        encryptData = RSAEncrypt.encrypt((RSAPrivateKey) key.getPrivate(), rawData);
        log.info("Encrypt Data(privateKey) done![time: {}ms]", new Date().getTime() - time);
        log.info("Decrypt Data(publicKey)...[encryptDataLength: {}B]", encryptData.length);
        time = new Date().getTime();
        Assert.assertArrayEquals(rawData, RSAEncrypt.decrypt((RSAPublicKey) key.getPublic(), encryptData));
        log.info("Decrypt Data(publicKey) done![time: {}ms]", new Date().getTime() - time);
    }

    @Test
    public void ByteToKeyTest() throws
            InvalidKeySpecException,
            IllegalBlockSizeException,
            InvalidKeyException,
            BadPaddingException,
            NoSuchPaddingException {
        KeyPair keyPair = RSAEncrypt.getKeyPair(2048);
        byte[] data = new byte[128];
        new Random().nextBytes(data);
        byte[] encryptData;
        byte[] decryptData;
        encryptData = RSAEncrypt.encrypt(RSAEncrypt.bytesToRSAPrivateKey(keyPair.getPrivate().getEncoded()), data);
        decryptData = RSAEncrypt.decrypt(RSAEncrypt.bytesToRSAPublicKey(keyPair.getPublic().getEncoded()), encryptData);
        Assert.assertArrayEquals(data, decryptData);

    }


}
