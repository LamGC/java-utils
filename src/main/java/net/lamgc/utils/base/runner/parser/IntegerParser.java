package net.lamgc.utils.base.runner.parser;

import net.lamgc.utils.base.runner.StringParameterParser;

public class IntegerParser implements StringParameterParser<Integer> {
    @Override
    public Integer parse(String strValue) {
        return Integer.parseInt(strValue);
    }

    @Override
    public Integer defaultValue() {
        return 0;
    }
}
