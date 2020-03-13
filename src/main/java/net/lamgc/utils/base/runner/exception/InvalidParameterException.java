package net.lamgc.utils.base.runner.exception;

/**
 * 参数无效异常
 */
public class InvalidParameterException extends RunnerException {

    public InvalidParameterException(String message){
        super(TRIGGER_DEVELOPER, message);
    }

    public InvalidParameterException(Throwable cause){
        super(TRIGGER_DEVELOPER, cause);
    }

    public InvalidParameterException(String message, Throwable cause){
        super(TRIGGER_DEVELOPER, message, cause);
    }

}
