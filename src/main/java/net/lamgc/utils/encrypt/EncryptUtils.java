package net.lamgc.utils.encrypt;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Objects;

public final class EncryptUtils {

    /**
     * 字节数组转SecretKey
     * @param keyEncode 密钥编码数据
     * @param algorithm 密钥所属算法
     * @return SecretKey对象
     */
    public static SecretKey BytesToSecretKey(byte[] keyEncode, String algorithm){
        Objects.requireNonNull(keyEncode);
        Objects.requireNonNull(algorithm);
        return new SecretKeySpec(keyEncode, algorithm);
    }

}
