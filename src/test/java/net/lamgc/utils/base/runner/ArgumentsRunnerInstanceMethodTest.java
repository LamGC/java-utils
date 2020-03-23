package net.lamgc.utils.base.runner;

import com.google.common.base.Defaults;
import net.lamgc.utils.base.runner.exception.InvalidParameterException;
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

    @Test
    public void ignoredCommandCaseTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.setCommandIgnoreCase(true);
        Assert.assertEquals(new ArgumentsRunner(InstanceRunnerTestMain.class, config).run(testMain, new String[]{"ignoredcommandCasetest"}), "ignoredCommandCase");
    }

    @Test
    public void customTrueFlagTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.addTrueFlag("1");
        Assert.assertTrue((Boolean) new ArgumentsRunner(InstanceRunnerTestMain.class, config).run(testMain, "customTrueFlagTest -flag=1".split(" ")));
    }

    @Test
    public void customBooleanParserTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.addStringParameterParser(new StringParameterParser<Boolean>() {
            @Override
            public Boolean parse(String strValue) {
                return strValue.equals("test");
            }

            @Override
            public Boolean defaultValue() {
                return Boolean.FALSE;
            }
        });
        Assert.assertTrue((Boolean) new ArgumentsRunner(InstanceRunnerTestMain.class, config).run(testMain, "customTrueFlagTest -flag=test".split(" ")));
    }

    @Test(expected = InvalidParameterException.class)
    public void strictDefaultCheckTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.setStrictDefaultCheck(true);
        new ArgumentsRunner(InstanceRunnerTestMain.class, config).run(testMain, "strictDefaultCheckTest".split(" "));
    }

    @Test
    public void nonStrictDefaultCheckTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.setStrictDefaultCheck(false);
        Assert.assertEquals(
                new ArgumentsRunner(InstanceRunnerTestMain.class, config)
                        .run(testMain, "strictDefaultCheckTest".split(" ")),
                Defaults.defaultValue(Integer.TYPE));
    }

    @Test
    public void useDefaultValueInsteadOfExceptionTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.setUseDefaultValueInsteadOfException(true);
        new ArgumentsRunner(StaticRunnerTestMain.class, config).run(new String[] {"argumentConvertTest", "-num=test"});
    }

    @Test
    public void customStringParameterParserTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.addStringParameterParser(new StringParameterParser<Date>() {
            @Override
            public Date parse(String strValue) {
                return new Date(Long.parseLong(strValue));
            }

            @Override
            public Date defaultValue() {
                return null;
            }
        });
        new ArgumentsRunner(StaticRunnerTestMain.class, config).run(("customStringParameterParserTest -date=" + System.currentTimeMillis()).split(" "));
    }

}
