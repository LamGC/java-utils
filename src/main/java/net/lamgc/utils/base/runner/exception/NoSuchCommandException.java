package net.lamgc.utils.base.runner.exception;

public class NoSuchCommandException extends UserRunnerException {

    private String commandName;

    public NoSuchCommandException(String commandName) {
        super("No method found for command: " + commandName);
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
