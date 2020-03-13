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
        testContent.put("100", "int");
        testContent.put("100", "java.lang.Integer");
        testContent.put("100", "short");
        testContent.put("100", "java.lang.Short");
        testContent.put("1000000000000", "long");
        testContent.put("1000000000000", "java.lang.Long");
        testContent.put("3.14", "float");
        testContent.put("3.14", "java.lang.Float");
        testContent.put("3.1415926", "double");
        testContent.put("3.1415926", "java.lang.Double");
        testContent.put("test", "java.lang.String");

        testContent.forEach((content, type) -> {
            Object result = StringParser.getObjectTypeParser(type).parser.parse(content);
            log.info("Type {} Result Type: {}, Result: {}", type, result.getClass().getSimpleName(), result);
            Assert.assertEquals(content, String.valueOf(result));
        });
    }

}
