package net.lamgc.utils.base.runner;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

class ArgumentsRunnerConfig implements Serializable {

    /**
     * 命令是否忽略大小写
     */
    private boolean commandIgnoreCase = false;

    /**
     * 是否需要参数标识(--参数 参数值).
     * @deprecated 尚未启用.
     */
    private boolean argumentsFlag = true;

    /**
     * 严格的默认值检查.
     * 当出现参数force=false但defaultValue为空的情况下, 将抛出异常而不是设置类型默认值.
     */
    private boolean strictDefaultCheck = false;

    /**
     * true的标识名.<br/>
     * 参数类型为Boolean且参数值存在的情况下, 将会判断值是否存在于trueFlag中, 如果存在, 则认为是true值.
     */
    private Set<String> trueFlag = new HashSet<>(4);

    /**
     * StringParameterParser存储对象
     */
    private StringParameterParserMap parameterParserMap = new StringParameterParserMap();

    public ArgumentsRunnerConfig() {
        initialConfig();
    }

    /**
     * 构造时会调用的初始化方法.<br/>
     * 可覆盖.
     */
    private void initialConfig() {
        trueFlag.add("t");
        trueFlag.add("true");
        trueFlag.add("y");
        trueFlag.add("yes");


    }

    /**
     * 添加指定Type的Parser对象.<br/>
     * 注意: 添加会导致原本的Parser被覆盖.
     * @param parser Parser对象
     */
    public void addStringParameterParser(StringParameterParser<? extends Type> parser) {
        this.parameterParserMap.addParser(parser);
    }

    /**
     * 删除指定Type的Parser对象.<br/>
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
    public StringParameterParser<? extends Type> getStringParameterParser(Type type) {
        return this.parameterParserMap.getParser(type);
    }

    /**
     * 添加一个代表true的值.
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
     * 获得当前的trueFlagSet.<br/>
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
     * 命令是否忽略大小写.<br/>
     * 默认值: false
     *
     * @param commandIgnoreCase 默认为false, true则为忽略大小写,
     *                          需要随着构造方法传入才会生效.
     */
    public void setCommandIgnoreCase(boolean commandIgnoreCase) {
        this.commandIgnoreCase = commandIgnoreCase;
    }

    /**
     * 参数是否需要标识.
     * @return true则参数必须要有标识
     */
    public boolean isArgumentsFlag() {
        return argumentsFlag;
    }

    /**
     * 参数是否需要标识.<br/>
     * 默认值: true
     * <br/>
     * 当参数需要标识时, 命令行可不按参数顺序传递参数.<br/>
     * 如过参数不需要标识时, 命令行必须按照命令对应方法的参数顺序传参, 否则可能导致转换失败导致出错.<br/>
     * @param argumentsFlag 是否需要参数标识, 默认true
     */
    public void setArgumentsFlag(boolean argumentsFlag) {
        this.argumentsFlag = argumentsFlag;
    }

    public boolean isStrictDefaultCheck() {
        return strictDefaultCheck;
    }

    public void setStrictDefaultCheck(boolean strictDefaultCheck) {
        this.strictDefaultCheck = strictDefaultCheck;
    }
}
