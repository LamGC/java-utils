package net.lamgc.utils.base.runner;

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Random;

public class InstanceRunnerTestMain {

    private final int id = new Random().nextInt();

    private final Logger log = LoggerFactory.getLogger("RunnerMain-id" + id);

    public static void main(String[] args) {
        ArgumentsRunner.runInThisClass(new InstanceRunnerTestMain(), args);
    }

    @Command(commandName = "help", defaultCommand = true)
    public static Boolean noParameterDefaultCommand() {
        System.out.println("Usage: java -jar jarFile <Command> [Arguments...]");
        return Boolean.TRUE;
    }

    @Command(commandName = "test-01")
    public void test_01(
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
    public void printTimeByTimeStamp(@Argument long timeStamp) {
        log.info("Time: {}", new Date(timeStamp));
    }

    @Command
    public void implicitCommandName() {
        log.info("test_02被调用");
    }

    @Command(commandName = "forceParamTest")
    public void forceParamTest(@Argument(name = "arg1") String arg1) {
        Assert.fail();
    }

}
