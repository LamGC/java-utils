package net.lamgc.utils.base.runner;

public interface StringParameterParser<R> {

    R parse(final String strValue) throws Exception;

    R defaultValue();

}
