package net.lamgc.utils.base.runner.exception;

public class ParseParameterException extends UserRunnerException {

    private String parameterName;
    private String parameterValue;
    private String typeName;

    public ParseParameterException(String paramName, String paramValue, String typeName, Throwable cause) {
        super(paramName + " => " + paramValue + "(" + typeName + ")", cause);
        this.parameterName = paramName;
        this.parameterValue = paramValue;
        this.typeName = typeName;
    }

    /**
     * 获取引发异常的参数名
     * @return 参数名
     */
    public String getParameterName() {
        return parameterName;
    }

    /**
     * 获取参数传入的值
     * @return 用户传入的参数String值
     */
    public String getParameterValue() {
        return parameterValue;
    }

    /**
     * 获取形参的类型.
     * @return 形参的类型.
     */
    public String getTypeName() {
        return typeName;
    }
}
