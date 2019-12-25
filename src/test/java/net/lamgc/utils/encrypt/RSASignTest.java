package net.lamgc.utils.encrypt;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RSASignTest {

    private RSASign.SignAlgorithm algorithm = RSASign.SignAlgorithm.SHA512;

    @Test
    public void signTest() throws InvalidKeySpecException, SignatureException, InvalidKeyException {
        byte[] data = new byte[5 * 1024];
        KeyPair keyPair = RSAEncrypt.getKeyPair(3072);
        new Random().nextBytes(data);
        byte[] signValue = RSASign.sign(data, (RSAPrivateKey) keyPair.getPrivate(), algorithm);
        Assert.assertTrue("Check Sign failed",
                RSASign.checkSign(data, signValue, (RSAPublicKey) keyPair.getPublic(), algorithm));
    }

    @Test
    public void badSignTest() throws InvalidKeySpecException, InvalidKeyException, SignatureException {
        byte[] data = new byte[5 * 1024];
        KeyPair keyPair = RSAEncrypt.getKeyPair(3072);
        new Random().nextBytes(data);
        byte[] signValue = RSASign.sign(data, (RSAPrivateKey) keyPair.getPrivate(), algorithm);
        new Random().nextBytes(data);
        Assert.assertFalse("Check Sign failed",
                RSASign.checkSign(data, signValue, (RSAPublicKey) keyPair.getPublic(), algorithm));
    }

}
