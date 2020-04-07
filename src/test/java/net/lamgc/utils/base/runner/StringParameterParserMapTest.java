package net.lamgc.utils.base.runner;

import org.junit.Assert;
import org.junit.Test;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringParameterParserMapTest {

    @Test
    public void allTest() {
        StringParameterParserMap map = new StringParameterParserMap();
        StringParameterParser<Date> parser = new DateParser();
        map.addParser(parser);
        Assert.assertTrue(map.hasParser(parser.defaultValue().getClass()));
        Assert.assertEquals(parser, map.getParser(parser.defaultValue().getClass()));
        Assert.assertEquals(parser, map.removeParser(parser.defaultValue().getClass()));
        Assert.assertFalse(map.hasParser(parser.defaultValue().getClass()));
    }

    public static class DateParser implements StringParameterParser<Date>, Serializable {
        @Override
        public Date parse(String strValue) throws Exception {
            return new SimpleDateFormat().parse(strValue);
        }

        @Override
        public Date defaultValue() {
            return new Date();
        }
    }

}
