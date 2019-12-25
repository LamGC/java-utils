package net.lamgc.utils.encrypt;

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Random;

public class RSAEncryptWithAESTest {

    @Test
    public void privateKeyEncryptTest() throws InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        byte[] data = new byte[512];
        new Random().nextBytes(data);
        KeyPair keyPair = RSAEncrypt.getKeyPair(3072);

        byte[] encryptData = RSAEncryptWithAES.encrypt(data, (RSAPrivateKey) keyPair.getPrivate());

        Assert.assertArrayEquals(data, RSAEncryptWithAES.decrypt(encryptData, (RSAPublicKey) keyPair.getPublic()));
    }

    @Test
    public void publicKeyEncryptTest() throws InvalidKeyException, BadPaddingException, NoSuchPaddingException, IllegalBlockSizeException {
        byte[] data = new byte[512];
        new Random().nextBytes(data);
        KeyPair keyPair = RSAEncrypt.getKeyPair(3072);

        byte[] encryptData = RSAEncryptWithAES.encrypt(data, (RSAPublicKey) keyPair.getPublic());

        Assert.assertArrayEquals(data, RSAEncryptWithAES.decrypt(encryptData, (RSAPrivateKey) keyPair.getPrivate()));
    }

}
