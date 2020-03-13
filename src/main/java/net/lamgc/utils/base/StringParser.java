package net.lamgc.utils.base;

public enum StringParser {
    INT((ParserInterface<String, Integer>) Integer::parseInt),
    SHORT((ParserInterface<String, Short>) Short::parseShort),
    LONG((ParserInterface<String, Long>) Long::parseLong),
    FLOAT((ParserInterface<String, Float>) Float::parseFloat),
    DOUBLE((ParserInterface<String, Double>) Double::parseDouble),
    CHAR((ParserInterface<String, Character>) s -> {
        if(s.length() == 1) {
            return s.toCharArray()[0];
        } else {
            throw new IllegalArgumentException("String length is not 1 (length: " + s.length() + "): " + s);
        }
    }),
    STRING((ParserInterface<String, String>) s -> s),
    ;

    public final ParserInterface<String, ?> parser;

    StringParser(ParserInterface<String, ?> parser) {
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
    public interface ParserInterface <T, R>  {
        R parse(T content);
    }

}