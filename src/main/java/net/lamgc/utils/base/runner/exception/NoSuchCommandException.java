package net.lamgc.utils.base.runner.exception;

public class NoSuchCommandException extends RunnerException {
    public NoSuchCommandException(String commandName) {
        super(TRIGGER_USER, "No method found for command: " + commandName);
    }
}
