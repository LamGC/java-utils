package net.lamgc.utils.base;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringParserTest {

    private final static Logger log = LoggerFactory.getLogger("StringParserTest");

    @Test
    public void parseTest() {
        Multimap<String, String> testContent = HashMultimap.create();
        testContent.put("100", Integer.TYPE.getTypeName());
        testContent.put("100", Integer.class.getTypeName());
        testContent.put("100", Short.TYPE.getTypeName());
        testContent.put("100", Short.class.getTypeName());
        testContent.put("1000000000000", Long.TYPE.getTypeName());
        testContent.put("1000000000000", Long.class.getTypeName());
        testContent.put("3.14", Float.TYPE.getTypeName());
        testContent.put("3.14", Float.class.getTypeName());
        testContent.put("3.1415926", Double.TYPE.getTypeName());
        testContent.put("3.1415926", Double.class.getTypeName());
        testContent.put("test", String.class.getTypeName());
        testContent.put("a", Character.TYPE.getTypeName());
        testContent.put("a", Character.class.getTypeName());

        testContent.forEach((content, type) -> {
            Object result = StringParser.getObjectTypeParser(type).parser.parse(content);
            log.info("Type {} Result Type: {}, Result: {}", type, result.getClass().getSimpleName(), result);
            Assert.assertEquals(content, String.valueOf(result));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsupportedTypeTest() {
        StringParser.getObjectTypeParser("null");
    }

    @Test(expected = IllegalArgumentException.class)
    public void badCharacterTest() {
        StringParser.getObjectTypeParser(Character.class.getTypeName()).parser.parse("test");
    }

}
