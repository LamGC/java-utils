package net.lamgc.utils.base.runner;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class StaticRunnerTestMain {

    private final static Logger log = LoggerFactory.getLogger("RunnerMain");

    public static void main(String[] args) {
        ArgumentsRunner.runInThisClass(args);
    }

    @Command(commandName = "help", defaultCommand = true)
    public static Boolean noParameterDefaultCommand() {
        System.out.println("Usage: java -jar jarFile <Command> [Arguments...]");
        return Boolean.TRUE;
    }

    @Command(commandName = "test-01")
    public static void test_01(
            @Argument(name = "propName") String propName,
            @Argument(name = "length") int length,
            @Argument(name = "port") short port,
            @Argument(name = "percentage", force = false, defaultValue = "0.0") float percentage,
            @Argument(name = "bool") boolean bool,
            @Argument(name = "pi", force = false, defaultValue = "3.1415926") double pi
    ) {
        log.info("PropName: {}, Length: {}, port: {}, percentage: {}, bool: {}, pi: {}", propName, length, port, percentage, bool, pi);
    }

    @Command(commandName = "printTime")
    public static void printTimeByTimeStamp(@Argument long timeStamp) {
        log.info("Time: {}", new Date(timeStamp));
    }

    @Command
    public static void implicitCommandName() {
        log.info("test_02被调用");
    }

    @Command(commandName = "forceParamTest")
    public static void forceParamTest(@Argument(name = "arg1") String arg1) {
        Assert.fail();
    }

}
