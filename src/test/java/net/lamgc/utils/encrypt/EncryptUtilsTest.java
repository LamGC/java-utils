package net.lamgc.utils.encrypt;

import org.junit.Test;

import java.util.Random;

public class EncryptUtilsTest {

    @Test(expected = IllegalStateException.class)
    public void invalidAlgorithmTest() {
        byte[] keySeed = new byte[128];
        new Random().nextBytes(keySeed);
        EncryptUtils.getSecretKey(keySeed, 128, "NONE");
    }

}
