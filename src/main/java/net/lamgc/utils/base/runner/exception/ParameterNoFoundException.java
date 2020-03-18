package net.lamgc.utils.base.runner.exception;

public class ParameterNoFoundException extends UserRunnerException {

    private String methodName;
    private int parameterIndex;
    private String parameterName = null;

    public ParameterNoFoundException(String methodName, int parameterIndex) {
        super("MethodName: " + methodName + ", Index: " + parameterIndex);
        this.methodName = methodName;
        this.parameterIndex = parameterIndex;
    }

    public ParameterNoFoundException(String methodName, int parameterIndex, String parameterName) {
        super("MethodName: " + methodName + ", Index: " + parameterIndex + ", ParameterName: " + parameterName);
        this.methodName = methodName;
        this.parameterIndex = parameterIndex;
        this.parameterName = parameterName;
    }

    /**
     * 获取方法名
     * @return 引发本异常的方法名
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * 获取缺失参数在参数列表的索引
     * @return 参数索引
     */
    public int getParameterIndex() {
        return parameterIndex;
    }

    /**
     * 获取该参数的参数名.
     * @return 缺失参数的名称
     */
    public String getParameterName() {
        return parameterName;
    }
}
