package net.lamgc.utils.base.runner.exception;

/**
 * 当用户未指定命令, 且未设置defaultCommand时抛出.<br/>
 * 该异常属于用户引发异常.
 */
public class CommandNotSpecifiedException extends UserRunnerException {
    public CommandNotSpecifiedException() {
        super("The command to run is not specified in the parameter");
    }
}
