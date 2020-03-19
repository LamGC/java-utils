package net.lamgc.utils.base.runner;

@FunctionalInterface
public interface StringParameterParser<R> {

    R parse(String strValue);

}
