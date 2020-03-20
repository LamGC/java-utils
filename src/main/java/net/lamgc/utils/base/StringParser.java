package net.lamgc.utils.base;

/**
 * 字符串转换器.
 */
public enum StringParser {
    INT((ParserInterface<Integer>) Integer::parseInt),
    SHORT((ParserInterface<Short>) Short::parseShort),
    LONG((ParserInterface<Long>) Long::parseLong),
    FLOAT((ParserInterface<Float>) Float::parseFloat),
    DOUBLE((ParserInterface<Double>) Double::parseDouble),
    CHAR((ParserInterface<Character>) s -> {
        if(s.length() == 1) {
            return s.toCharArray()[0];
        } else {
            throw new IllegalArgumentException("String length is not 1 (length: " + s.length() + "): " + s);
        }
    }),
    STRING((ParserInterface<String>) s -> s),
    ;

    public final ParserInterface<?> parser;

    StringParser(ParserInterface<?> parser) {
        this.parser = parser;
    }

    public static StringParser getObjectTypeParser(String typeName) {
        String typeClassName = typeName.substring(typeName.lastIndexOf(".") + 1);
        for (StringParser value : values()) {
            if(value.name().toLowerCase().contains(typeClassName.toLowerCase()) ||
                    typeClassName.toLowerCase().contains(value.name().toLowerCase())) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unsupported Type: " + typeName);
    }

    @FunctionalInterface
    public interface ParserInterface <R>  {
        R parse(String content);
    }

}