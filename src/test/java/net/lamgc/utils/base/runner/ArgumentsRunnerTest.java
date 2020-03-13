package net.lamgc.utils.base.runner;

import net.lamgc.utils.base.runner.exception.ParameterNoFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class ArgumentsRunnerTest {

    private final static Logger log = LoggerFactory.getLogger("ArgumentsRunnerTest");

    @Test
    public void normalOperationTest() {
        ArgumentsRunner.run(RunnerTestMain.class, ("test-01 -propName test --length:5 /port=8080 -percentage 0.55 -bool -pi 3.1415926535898").split(" "));
    }

    @Test
    public void runDefaultCommandTest() {
        Assert.assertTrue((Boolean) ArgumentsRunner.run(RunnerTestMain.class, new String[0]));
    }

    @Test
    public void runCommandWithoutIdentificationTest() {
        ArgumentsRunner.run(RunnerTestMain.class, new String[] {"implicitCommandName"});
    }

    @Test(expected = ParameterNoFoundException.class)
    public void missingForceParameterTest() {
        ArgumentsRunner.run(RunnerTestMain.class, new String[] {"forceParamTest"});
    }

    @Test
    public void implicitParameterNameTest() {
        ArgumentsRunner.run(RunnerTestMain.class, ("printTime --timeStamp " + (new Date().getTime() * 1000)).split(" "));
    }

    @Test
    public void runInThisClassTest() {
        RunnerTestMain.main(new String[0]);
    }

}
