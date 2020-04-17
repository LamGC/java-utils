package net.lamgc.utils.base.runner.parser;

import net.lamgc.utils.base.runner.StringParameterParser;

public class CharParser implements StringParameterParser<Character> {
    @Override
    public Character parse(String strValue) {
        String str = strValue.trim();
        if(str.length() != 1) {
            throw new IllegalArgumentException("String length is not 1 (length: " + str.length() + "): " + str);
        }
        return str.charAt(0);
    }

    @Override
    public Character defaultValue() {
        return Character.MIN_VALUE;
    }
}
