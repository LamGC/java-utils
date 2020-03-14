package net.lamgc.utils.base.runner.exception;

/**
 * 参数无效异常
 */
public class InvalidParameterException extends DeveloperRunnerException {

    public InvalidParameterException(String message){
        super(message);
    }

    public InvalidParameterException(Throwable cause){
        super(cause);
    }

    public InvalidParameterException(String message, Throwable cause){
        super(cause);
    }

}
