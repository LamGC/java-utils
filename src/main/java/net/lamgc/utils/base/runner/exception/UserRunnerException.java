package net.lamgc.utils.base.runner.exception;

/**
 * 由用户引发的异常
 */
public class UserRunnerException extends RunnerException {
    public UserRunnerException(String message) {
        super(TRIGGER_USER, message);
    }

    public UserRunnerException(Throwable cause) {
        super(TRIGGER_USER, cause);
    }

    public UserRunnerException(String message, Throwable cause) {
        super(TRIGGER_USER, message, cause);
    }
}
