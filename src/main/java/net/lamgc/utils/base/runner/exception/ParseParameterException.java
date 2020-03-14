package net.lamgc.utils.base.runner.exception;

public class ParseParameterException extends UserRunnerException {
    public ParseParameterException(String paramName, String paramValue, String typeName,Throwable cause) {
        super(paramName + " => " + paramValue + "(" + typeName + ")", cause);
    }
}
