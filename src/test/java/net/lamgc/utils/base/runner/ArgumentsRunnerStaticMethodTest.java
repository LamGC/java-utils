package net.lamgc.utils.base.runner;

import com.google.common.base.Defaults;
import net.lamgc.utils.base.runner.exception.DeveloperRunnerException;
import net.lamgc.utils.base.runner.exception.InvalidParameterException;
import net.lamgc.utils.base.runner.exception.ParameterNoFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

public class ArgumentsRunnerStaticMethodTest {

    @Test
    public void getCommandNamesTest() {
        Set<String> commandNames = new ArgumentsRunner(StaticRunnerTestMain.class).getCommandNames();
        String[] commandNamesArr = new String[commandNames.size()];
        commandNames.toArray(commandNamesArr);
        LoggerFactory.getLogger(ArgumentsRunnerStaticMethodTest.class)
                .info("Commands: {}", Arrays.toString(commandNamesArr));
    }

    @Test
    public void normalOperationTest() {
        ArgumentsRunner.run(StaticRunnerTestMain.class,
                ("test-01 -propName test --length:5 -port=8080 -percentage 0.55 -bool -pi 3.1415926535898 -bool2=true")
                        .split(" "));
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
        ArgumentsRunner.run(StaticRunnerTestMain.class,
                ("printTime --timeStamp " + (new Date().getTime() * 1000)).split(" "));
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
        Assert.assertEquals("ignoredCommandCase", new ArgumentsRunner(StaticRunnerTestMain.class, config)
                .run(new String[]{"ignoredcommandCasetest"}));
    }

    @Test
    public void customTrueFlagTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.addTrueFlag("1");
        Assert.assertTrue((Boolean) new ArgumentsRunner(StaticRunnerTestMain.class, config)
                .run("customTrueFlagTest -flag=1 -flag2:2".split(" ")));
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
        Assert.assertTrue((Boolean) new ArgumentsRunner(StaticRunnerTestMain.class, config)
                .run(new String[] {"customTrueFlagTest", "-flag=test", "-flag2=test2"}));
    }

    @Test
    public void noValueTrueJudgmentTest() {
        Assert.assertTrue((Boolean) new ArgumentsRunner(StaticRunnerTestMain.class)
                .run(new String[] {"customTrueFlagTest", "-test", "value1", "-flag"}));
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
        Assert.assertEquals(Defaults.defaultValue(Integer.TYPE),
                new ArgumentsRunner(StaticRunnerTestMain.class, config)
                    .run("strictDefaultCheckTest".split(" ")));
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
        new ArgumentsRunner(StaticRunnerTestMain.class, config)
                .run(("customStringParameterParserTest -date=" + System.currentTimeMillis()).split(" "));
    }

    @Test(expected = InvalidParameterException.class)
    public void unsupportedParameterTypeTest() {
        ArgumentsRunnerConfig config = new ArgumentsRunnerConfig();
        config.removeStringParameterParser(Long.TYPE);
        new ArgumentsRunner(StaticRunnerTestMain.class, config)
                .run(("printTime -timeStamp=" + System.currentTimeMillis()).split(" "));
    }

    @Test(expected = InvalidParameterException.class)
    public void noAnnotationArgumentsTest() {
        ArgumentsRunner.run(StaticRunnerTestMain.class, new String[] {"noAnnotationArgumentsTest"});
    }

    @Test(expected = DeveloperRunnerException.class)
    public void throwExceptionTest() {
        ArgumentsRunner.run(StaticRunnerTestMain.class, new String[] {"throwExceptionTest"});
    }

}
