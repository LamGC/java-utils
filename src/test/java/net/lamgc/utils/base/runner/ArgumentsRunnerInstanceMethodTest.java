package net.lamgc.utils.base.runner;

import net.lamgc.utils.base.runner.exception.ParameterNoFoundException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ArgumentsRunnerInstanceMethodTest {

    private static InstanceRunnerTestMain testMain = new InstanceRunnerTestMain();
    
    @Test
    public void normalOperationTest() {
        ArgumentsRunner.run(InstanceRunnerTestMain.class, testMain,
                ("test-01 -propName test --length:5 /port=8080 -percentage 0.55 -bool -pi 3.1415926535898 -bool2=true").split(" "));
    }

    @Test
    public void runDefaultCommandTest() {
        Assert.assertTrue((Boolean) ArgumentsRunner.run(InstanceRunnerTestMain.class, testMain,  new String[0]));
    }

    @Test
    public void runCommandWithoutIdentificationTest() {
        ArgumentsRunner.run(InstanceRunnerTestMain.class, testMain,  new String[] {"implicitCommandName"});
    }

    @Test(expected = ParameterNoFoundException.class)
    public void missingForceParameterTest() {
        ArgumentsRunner.run(InstanceRunnerTestMain.class, testMain,  new String[] {"forceParamTest"});
    }

    @Test
    public void implicitParameterNameTest() {
        ArgumentsRunner.run(InstanceRunnerTestMain.class, testMain,  ("printTime --timeStamp " + (new Date().getTime() * 1000)).split(" "));
    }

    @Test
    public void runInThisClassTest() {
        InstanceRunnerTestMain.main(new String[0]);
    }

    @Test
    public void preParameterNullTest() {
        ArgumentsRunner.run(InstanceRunnerTestMain.class, testMain,
                "preParameterNullTest -nonNullParam1 test -nonNullParam2 test2 -nonNullParam3 test3".split(" "));
    }

}
