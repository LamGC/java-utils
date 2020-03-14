package net.lamgc.utils.base.runner.exception;

public class ParameterNoFoundException extends UserRunnerException {

    public ParameterNoFoundException(String methodName, int parameterIndex) {
        super("MethodName: " + methodName + ", Index: " + parameterIndex);
    }

    public ParameterNoFoundException(String methodName, int parameterIndex, String parameterName) {
        super("MethodName: " + methodName + ", Index: " + parameterIndex + ", ParameterName: " + parameterName);
    }
}
