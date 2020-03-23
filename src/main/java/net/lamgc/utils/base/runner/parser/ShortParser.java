package net.lamgc.utils.base.runner.parser;

import net.lamgc.utils.base.runner.StringParameterParser;

public class ShortParser implements StringParameterParser<Short> {
    @Override
    public Short parse(String strValue) {
        return Short.parseShort(strValue);
    }

    @Override
    public Short defaultValue() {
        return 0;
    }
}
