package net.lamgc.utils.base.runner.exception;

public class IllegalModifierException extends RunnerException {
    public IllegalModifierException(String message) {
        super(TRIGGER_DEVELOPER, message);
    }
}
