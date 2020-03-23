package net.lamgc.utils.base.runner.parser;

import net.lamgc.utils.base.runner.StringParameterParser;

public class DoubleParser implements StringParameterParser<Double> {
    @Override
    public Double parse(String strValue) {
        return Double.parseDouble(strValue);
    }

    @Override
    public Double defaultValue() {
        return 0D;
    }
}
