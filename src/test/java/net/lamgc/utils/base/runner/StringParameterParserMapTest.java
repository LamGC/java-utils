package net.lamgc.utils.base.runner;

import net.lamgc.utils.base.runner.parser.IntegerParser;
import org.junit.Assert;
import org.junit.Test;

public class StringParameterParserMapTest {

    @Test
    public void allTest() {
        StringParameterParserMap map = new StringParameterParserMap();
        IntegerParser parser = new IntegerParser();
        map.addParser(parser);
        Assert.assertTrue(map.hasParser(parser.defaultValue().getClass()));
        Assert.assertEquals(parser, map.getParser(parser.defaultValue().getClass()));
        Assert.assertEquals(parser, map.removeParser(parser.defaultValue().getClass()));
        Assert.assertFalse(map.hasParser(parser.defaultValue().getClass()));
    }

}
