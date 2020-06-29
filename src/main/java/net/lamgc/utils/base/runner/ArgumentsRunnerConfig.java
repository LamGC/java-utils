package net.lamgc.utils.base.runner;

import net.lamgc.utils.base.runner.parser.*;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class ArgumentsRunnerConfig implements Serializable {

    /**
     * 命令是否忽略大小写.
     */
    private boolean commandIgnoreCase = false;

    /**
     * 严格的默认值检查.
     * 当出现参数force=false但defaultValue为空的情况下, 将抛出异常而不是设置类型默认值.
     */
    private boolean strictDefaultCheck = false;

    /**
     * true的标识名.<br>
     * 参数类型为Boolean且参数值存在的情况下, 将会判断值是否存在于trueFlag中, 如果存在, 则认为是true值.
     */
    private Set<String> trueFlag = new HashSet<>(4);

    /**
     * 转换异常时是否使用参数值而不是抛出异常.<br>
     * 默认值: false
     */
    private boolean useDefaultValueInsteadOfException = false;

    /**
     * StringParameterParser存储对象.
     */
    private StringParameterParserMap parameterParserMap = new StringParameterParserMap();

    public ArgumentsRunnerConfig() {
        initialConfig();
    }

    /**
     * 构造时会调用的初始化方法.<br>
     * 可覆盖.
     */
    private void initialConfig() {
        addTrueFlag("t");
        addTrueFlag("true");
        addTrueFlag("y");
        addTrueFlag("yes");

        addStringParameterParser(new IntegerParser());
        addStringParameterParser(new ShortParser());
        addStringParameterParser(new LongParser());
        addStringParameterParser(new FloatParser());
        addStringParameterParser(new DoubleParser());
        addStringParameterParser(new CharParser());
    }

    /**
     * 添加指定Type的Parser对象.<br>
     * 注意: 添加会导致原本的Parser被覆盖.<br>
     * 部分Type的处理: <br>
     * - 对Boolean的Parser: Config默认不存在对Boolean的转换, 如添加对Boolean的Parser将导致TrueFlag失效!<br>
     * - 对String的Parser: 如果存在对String类型的Parser, 将会提供参数原始值给Parser进行处理.
     * @param parser Parser对象
     */
    public void addStringParameterParser(StringParameterParser<?> parser) {
        this.parameterParserMap.addParser(parser);
    }

    /**
     * 删除指定Type的Parser对象.<br>
     * @param type 欲删除Parser对象的Type.
     */
    public void removeStringParameterParser(Type type) {
        this.parameterParserMap.removeParser(type);
    }

    /**
     * 获取指定Type的Parser对象.
     * @param type 欲获取的Parser对象所属的Type.
     * @return 如果获取成功返回对象, 失败返回null.
     */
    public StringParameterParser<?> getStringParameterParser(Type type) {
        return this.parameterParserMap.getParser(type);
    }

    /**
     * 指定Type是否存在Parser.
     * @param type 要检查的Type对象.
     * @return 如果存在, 返回true.
     */
    public boolean hasStringParameterParser(Type type) {
        return parameterParserMap.hasParser(type);
    }

    /**
     * 添加一个代表true的值.<br>
     * 注意: 当StringParameterParser添加了对Boolean对象的Parser时, TrueFlag的设置将会失效.<br>
     * @param flag true的代表参数值, 代表值将会经过 {@linkplain String#toLowerCase() toLowerCase()} 后存储
     */
    public void addTrueFlag(String flag) {
        trueFlag.add(flag.trim().toLowerCase());
    }

    /**
     * 删除已添加的true代表值.
     * @param flag 欲删除的true代表值.
     * @return 是否删除成功.
     */
    public boolean removeTrueFlag(String flag) {
        return trueFlag.remove(flag.trim().toLowerCase());
    }

    /**
     * 获得当前的trueFlagSet.<br>
     * 对通过本方法获取的Set对象的修改不会影响到trueFlag.
     * @return 存储当前已添加Flag的Set对象.
     */
    public Set<String> getTrueFlag() {
        return new HashSet<>(trueFlag);
    }

    /**
     * 命令是否忽略大小写.
     * @return true则为忽略
     */
    public boolean isCommandIgnoreCase() {
        return commandIgnoreCase;
    }

    /**
     * 命令是否忽略大小写.<br>
     * <br>
     * 默认值: false
     * @param commandIgnoreCase 默认为false, true则为忽略大小写,
     *                          需要随着构造方法传入才会生效.
     */
    public void setCommandIgnoreCase(boolean commandIgnoreCase) {
        this.commandIgnoreCase = commandIgnoreCase;
    }

    /**
     * 是否严格检查非force参数的默认值
     * @return 返回设定值, 默认为false
     */
    public boolean isStrictDefaultCheck() {
        return strictDefaultCheck;
    }

    /**
     * 设置是否严格检查参数force为false下的默认值.<br>
     * 当force为false且未设置defaultValue时, 如本项为false, 则返回默认值,
     * 如本项为true, 则抛出{@link net.lamgc.utils.base.runner.exception.InvalidParameterException InvalidParameterException}异常.<br>
     * <br>
     * 默认值: false
     * @param strictDefaultCheck 设定值.
     */
    public void setStrictDefaultCheck(boolean strictDefaultCheck) {
        this.strictDefaultCheck = strictDefaultCheck;
    }

    public boolean isUseDefaultValueInsteadOfException() {
        return useDefaultValueInsteadOfException;
    }

    public void setUseDefaultValueInsteadOfException(boolean useDefaultValueInsteadOfException) {
        this.useDefaultValueInsteadOfException = useDefaultValueInsteadOfException;
    }
}
