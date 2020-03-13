package net.lamgc.utils.base.runner.exception;

public class IllegalCommandException extends RunnerException {
    public IllegalCommandException(String message) {
        super(TRIGGER_DEVELOPER, message);
    }
}
