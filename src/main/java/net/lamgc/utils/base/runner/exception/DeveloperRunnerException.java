package net.lamgc.utils.base.runner.exception;

/**
 * 由开发者引发的异常
 */
public class DeveloperRunnerException extends RunnerException {
    public DeveloperRunnerException(String message) {
        super(TRIGGER_DEVELOPER, message);
    }

    public DeveloperRunnerException(Throwable cause) {
        super(TRIGGER_DEVELOPER, cause);
    }

    public DeveloperRunnerException(String message, Throwable cause) {
        super(TRIGGER_DEVELOPER, message, cause);
    }
}
