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
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.util.Date;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;

/**
 * 对{@link AESEncrypt}的加解密测试
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AESEncryptTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private static byte[] rawData = new byte[5 * 1024];

    private static int keySize = 128;

    private static SecretKey key = null;

    private static byte[] encryptData = null;

    @Test
    public void generateKeyTest() {
        byte[] data = new byte[256];
        new Random().nextBytes(data);
        AESEncrypt.getSecretKey(data, 0);
    }

    @Test
    public void A_encryptTest() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        new Random().nextBytes(rawData);
        log.info("Generate key...[keySize: {}]", keySize);
        long time = new Date().getTime();
        key = AESEncrypt.getSecretKey("TestKey", keySize);
        log.info("Generate key success![time: {}ms]", new Date().getTime() - time);
        log.info("Encrypt Data...[dataLength: {}B]", rawData.length);
        time = new Date().getTime();
        encryptData = AESEncrypt.encrypt(rawData, key);
        log.info("Encrypt Data done![time: {}ms, encryptDataLength: {}B]", new Date().getTime() - time, encryptData.length);
    }

    @Test
    public void B_decryptTest() throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchPaddingException {
        Assert.assertNotNull("key is null", key);
        Assert.assertNotNull("encryptDate is null", encryptData);
        log.info("Decrypt Data...[dataLength: {}B, keySize: {}]", rawData.length, keySize);
        long time = new Date().getTime();
        byte[] data = AESEncrypt.decrypt(encryptData, key);
        log.info("Decrypt Data done![time: {}ms, dataLength: {}B]", new Date().getTime() - time, data.length);
        assertArrayEquals(rawData, data);
    }


}
