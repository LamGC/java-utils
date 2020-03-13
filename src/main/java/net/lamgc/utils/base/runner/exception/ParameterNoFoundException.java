package net.lamgc.utils.base.runner.exception;

public class ParameterNoFoundException extends RunnerException {

    public ParameterNoFoundException(String methodName, int parameterIndex) {
        super(TRIGGER_USER, "MethodName: " + methodName + ", Index: " + parameterIndex);
    }

    public ParameterNoFoundException(String methodName, int parameterIndex, String parameterName) {
        super(TRIGGER_USER, "MethodName: " + methodName + ", Index: " + parameterIndex + ", ParameterName: " + parameterName);
    }
}
