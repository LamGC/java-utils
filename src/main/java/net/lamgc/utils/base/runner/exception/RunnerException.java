package net.lamgc.utils.base.runner.exception;

public class RunnerException extends RuntimeException {

    /**
     * 该异常由用户引起.<br/>
     * 当Trigger为该值时, 代表异常是由于用户输入原因导致的, 这种异常的出现是偶然的.
     */
    public final static int TRIGGER_USER = 1;

    /**
     * 该异常由开发者引起.<br/>
     * 当Trigger为该值时, 代表异常是由于代码层次原因导致的, 这种异常的出现是必然的.
     */
    public final static int TRIGGER_DEVELOPER = 2;

    private int exceptionTrigger;

    public RunnerException(int triggerType, String message){
        super(message);
        setExceptionTrigger(triggerType);
    }

    public RunnerException(int triggerType, Throwable cause) {
        super(cause.getMessage(), cause);
        setExceptionTrigger(triggerType);
    }

    public RunnerException(int triggerType, String message, Throwable cause){
        super(message, cause);
        setExceptionTrigger(triggerType);
    }

    /**
     * 获取异常触发者类型.
     * 本参数用于提示开发者该异常是由用户引起或者是开发者引起.
     * @return 参考 {@link #TRIGGER_DEVELOPER} 和 {@link #TRIGGER_USER}
     */
    public int getExceptionTrigger() {
        return exceptionTrigger;
    }

    private void setExceptionTrigger(int exceptionTrigger) {
        this.exceptionTrigger = exceptionTrigger;
    }
}
