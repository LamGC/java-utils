package net.lamgc.utils.base.runner;

import net.lamgc.utils.base.runner.exception.ParameterNoFoundException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class ArgumentsRunnerStaticMethodTest {

    @Test
    public void normalOperationTest() {
        ArgumentsRunner.run(StaticRunnerTestMain.class, ("test-01 -propName test --length:5 /port=8080 -percentage 0.55 -bool -pi 3.1415926535898 -bool2=true").split(" "));
    }

    @Test
    public void runDefaultCommandTest() {
        Assert.assertTrue((Boolean) ArgumentsRunner.run(StaticRunnerTestMain.class, new String[0]));
    }

    @Test
    public void runCommandWithoutIdentificationTest() {
        ArgumentsRunner.run(StaticRunnerTestMain.class, new String[] {"implicitCommandName"});
    }

    @Test(expected = ParameterNoFoundException.class)
    public void missingForceParameterTest() {
        ArgumentsRunner.run(StaticRunnerTestMain.class, new String[] {"forceParamTest"});
    }

    @Test
    public void implicitParameterNameTest() {
        ArgumentsRunner.run(StaticRunnerTestMain.class, ("printTime --timeStamp " + (new Date().getTime() * 1000)).split(" "));
    }

    @Test
    public void runInThisClassTest() {
        StaticRunnerTestMain.main(new String[0]);
    }

}
