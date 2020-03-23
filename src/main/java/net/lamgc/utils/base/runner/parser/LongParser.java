package net.lamgc.utils.base.runner.parser;

import net.lamgc.utils.base.runner.StringParameterParser;

public class LongParser implements StringParameterParser<Long> {
    @Override
    public Long parse(String strValue) {
        return Long.parseLong(strValue);
    }

    @Override
    public Long defaultValue() {
        return 0L;
    }
}
