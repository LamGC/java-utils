package net.lamgc.utils.base.runner;

import com.google.common.base.Defaults;
import net.lamgc.utils.base.runner.exception.InvalidParameterException;
import net.lamgc.utils.base.runner.exception.NoSuchCommandException;
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

    @Test
    public void preParameterNullTest() {
        ArgumentsRunner.run(InstanceRunnerTestMain.class,
                "preParameterNullTest -nonNullParam1 test -nonNullParam2 test2 -nonNullParam3 test3".split(" "));
    }

    @Test
    public void ignoredCommandCaseTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.setCommandIgnoreCase(true);
        Assert.assertEquals(new ArgumentsRunner(StaticRunnerTestMain.class, config).run(new String[]{"ignoredcommandCasetest"}), "ignoredCommandCase");
    }

    @Test
    public void customTrueFlagTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.addTrueFlag("1");
        Assert.assertTrue((Boolean) new ArgumentsRunner(StaticRunnerTestMain.class, config).run("customTrueFlagTest -flag=1".split(" ")));
    }

    @Test(expected = InvalidParameterException.class)
    public void strictDefaultCheckTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.setStrictDefaultCheck(true);
        new ArgumentsRunner(StaticRunnerTestMain.class, config).run("strictDefaultCheckTest".split(" "));
    }

    @Test
    public void nonStrictDefaultCheckTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.setStrictDefaultCheck(false);
        Assert.assertEquals(
                new ArgumentsRunner(StaticRunnerTestMain.class, config)
                    .run("strictDefaultCheckTest".split(" ")),
                Defaults.defaultValue(Integer.TYPE));
    }

}
