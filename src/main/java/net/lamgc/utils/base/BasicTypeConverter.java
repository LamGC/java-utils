package net.lamgc.utils.base;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * 基本数据类型Type 转 包装数据类型Type.<br>
 * 请使用{@link #valueOfBasicTypeName(Type)}方法获取.
 */
public enum BasicTypeConverter {
    BYTE(Byte.class),
    SHORT(Short.class),
    INT(Integer.class),
    LONG(Long.class),
    FLOAT(Float.class),
    DOUBLE(Double.class),
    BOOLEAN(Boolean.class),
    CHAR(Character.class)
    ;

    public final Type type;

    BasicTypeConverter(Type type) {
        this.type = type;
    }

    /**
     * 尝试转换基本数据类型为包装数据类型.<br>
     * 如没有对应包装数据类型将返回原Type对象.
     * @param baseType 欲转换的基本数据类型Type.
     * @return 如果为基本数据类型且存在包装数据类型则返回对应包装数据类型, 不存在则原样返回.
     */
    public static Type valueOfBasicTypeName(Type baseType) {
        try {
            return BasicTypeConverter.valueOf(Objects.requireNonNull(baseType).getTypeName().toUpperCase()).type;
        } catch(IllegalArgumentException e) {
            return baseType;
        }
    }

}
