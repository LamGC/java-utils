package net.lamgc.utils.encrypt;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;
import java.util.Objects;
import java.util.Random;

public class MessageDigestUtilsTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Test
    public void encryptTest(){
        byte[] data = new byte[1024];
        new Random().nextBytes(data);
        long time;
        for(MessageDigestUtils.Algorithm algorithm : MessageDigestUtils.Algorithm.values()){
            time = System.currentTimeMillis();
            byte[] digestValue = MessageDigestUtils.encrypt(data, algorithm);
            log.info("AlgorithmName: {}, Time: {}ms, MessageDigestResult: {}",
                    algorithm.algorithmName,
                    System.currentTimeMillis() - time,
                    new String(Base64.getEncoder()
                            .encode(Objects.requireNonNull(digestValue)))

            );
        }
    }

}
