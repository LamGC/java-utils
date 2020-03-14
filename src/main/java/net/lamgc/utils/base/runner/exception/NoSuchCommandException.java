package net.lamgc.utils.base.runner.exception;

public class NoSuchCommandException extends UserRunnerException {
    public NoSuchCommandException(String commandName) {
        super("No method found for command: " + commandName);
    }
}
