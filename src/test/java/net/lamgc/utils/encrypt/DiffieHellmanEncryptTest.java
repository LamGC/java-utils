package net.lamgc.utils.encrypt;

import org.junit.Assert;
import org.junit.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.util.Random;


public class DiffieHellmanEncryptTest {

    private final DiffieHellmanEncrypt.Algorithm ALGORITHM = DiffieHellmanEncrypt.Algorithm.AES;

    @Test
    public void encryptTest() throws InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
        DiffieHellmanEncrypt encrypt1 = new DiffieHellmanEncrypt();
        DiffieHellmanEncrypt encrypt2 = new DiffieHellmanEncrypt(DiffieHellmanEncrypt.DEFAULT_KEY_SIZE);

        final SecretKey key1 = encrypt1.getSecretKey(encrypt2.getPublicKey().getEncoded(), ALGORITHM);
        final SecretKey key2 = encrypt2.getSecretKey(encrypt1.getPublicKey().getEncoded(), ALGORITHM);

        Assert.assertArrayEquals(key1.getEncoded(), key2.getEncoded());

        byte[] data = new byte[512];
        new Random().nextBytes(data);

        byte[] encryptData = AESEncrypt.encrypt(data, key1);
        byte[] decryptData = AESEncrypt.decrypt(encryptData, key2);

        Assert.assertArrayEquals(data, decryptData);
    }

}
