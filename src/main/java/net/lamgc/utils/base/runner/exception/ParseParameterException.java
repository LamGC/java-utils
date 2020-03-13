package net.lamgc.utils.base.runner.exception;

public class ParseParameterException extends RunnerException {
    public ParseParameterException(String paramName, String paramValue, String typeName,Throwable cause) {
        super(TRIGGER_USER, paramName + " => " + paramValue + "(" + typeName + ")", cause);
    }
}
