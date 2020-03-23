package net.lamgc.utils.base.runner.parser;

import net.lamgc.utils.base.runner.StringParameterParser;

public class FloatParser implements StringParameterParser<Float> {
    @Override
    public Float parse(String strValue) {
        return Float.parseFloat(strValue);
    }

    @Override
    public Float defaultValue() {
        return 0F;
    }
}
