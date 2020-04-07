package net.lamgc.utils.encrypt;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

public class HmacEncryptUtilsTest {

    private final static Logger log = LoggerFactory.getLogger("HmacEncryptUtilsTest");

    @Test
    public void encryptTest() throws InvalidKeyException {
        Random random = new Random();
        byte[] data = new byte[512];
        random.nextBytes(data);

        byte[] keyRules = new byte[512];
        new SecureRandom().nextBytes(keyRules);
        long time;
        for (HmacEncryptUtils.Algorithm algorithm : HmacEncryptUtils.Algorithm.values()) {
            SecretKey key = HmacEncryptUtils.getSecretKey(keyRules, 512, algorithm);
            time = System.currentTimeMillis();
            byte[] digestValue = HmacEncryptUtils.encrypt(data, key, algorithm);
            log.info("AlgorithmName: {}, Time: {}ms, MessageDigestResult: {}",
                    algorithm.algorithmName,
                    System.currentTimeMillis() - time,
                    new String(Base64.getEncoder()
                            .encode(Objects.requireNonNull(digestValue)))
            );
        }
    }

}
