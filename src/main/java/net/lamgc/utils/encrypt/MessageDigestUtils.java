package net.lamgc.utils.encrypt;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class MessageDigestUtils {

    public static byte[] encrypt(byte[] data, Algorithm algorithm){
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance(Objects.requireNonNull(algorithm).algorithmName);
            digest.update(Objects.requireNonNull(data));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return digest.digest();
    }

    public enum Algorithm{
        MD2("MD2"),
        MD5("MD5"),
        SHA1("SHA-1"),
        SHA256("SHA-256"),
        SHA384("SHA-384"),
        SHA512("SHA-512")
        ;

        Algorithm(String algorithmName){
            this.algorithmName = algorithmName;
        }

        public String algorithmName;
    }

}
