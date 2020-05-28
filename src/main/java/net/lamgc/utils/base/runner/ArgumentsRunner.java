package net.lamgc.utils.base.runner;

import com.google.common.base.Defaults;
import com.google.common.base.Strings;
import net.lamgc.utils.base.ArgumentsProperties;
import net.lamgc.utils.base.runner.exception.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 命令行参数运行器.<br/>
 * 该运行器根据传入的参数, 自动选择指定类中带有{@linkplain Command Command标注}的方法, 且支持参数传入.<br/>
 * 运行格式:
 * <pre>
 *     java -jar jarFile <Command> [Arguments...]
 * </pre>
 */
public class ArgumentsRunner {


    private final static Pattern COMMAND_NAME_CHECK_PATTERN = Pattern.compile("^[^ .]+$");

    private final ArgumentsRunnerConfig config;

    private final Class<?> runClass;
    private CommandMap commandMap;

    /**
     * 无配置启动一个运行器.
     * @param runClass 待运行的Class对象
     * @param args 运行参数
     * @return 返回命令处理方法的返回值.
     * @throws RunnerException 当运行器发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public static Object run(Class<?> runClass, String[] args) {
        return new ArgumentsRunner(runClass, null).run(args);
    }

    /**
     * 无配置启动一个运行器.
     * @param runClass 待运行的Class对象
     * @param object class的实例对象
     * @param args 运行参数
     * @return 返回命令处理方法的返回值.
     * @throws RunnerException 当运行器发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public static Object run(Class<?> runClass, Object object, String[] args) throws RunnerException {
        return new ArgumentsRunner(runClass, null).run(object, args);
    }

    /**
     * 在调用该方法所在类启动ArgumentsRunner.<br/>
     * 注意: 当项目使用如SpringBoot之类的框架, 而框架存在重新打包项目并分隔目录的情况时, 请不要使用本方法!
     * 经过重新打包的Jar将无法根据堆栈找到 RunClass.
     * @param args 参数
     * @return 返回命令处理方法的返回值.
     * @throws RunnerException 当运行器发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public static Object runInThisClass(String[] args) throws RunnerException {
        return runInThisClass(null, args);
    }

    /**
     * 在调用该方法所在类启动ArgumentsRunner.<br/>
     * 注意: 当项目使用如SpringBoot之类的框架, 而框架存在重新打包项目并分隔目录的情况时, 请不要使用本方法!
     * 经过重新打包的Jar将无法根据堆栈找到 RunClass.
     * @param object 当前方法所在的class的实例对象
     * @param args 参数
     * @return 返回命令处理方法的返回值.
     * @throws RunnerException 当运行器发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public static Object runInThisClass(Object object, String[] args) throws RunnerException {
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            if(stackTraceElements.length < 3) {
                throw new DeveloperRunnerException(new IllegalStateException("Stack error"));
            }
            int callerElementIndex = 2;
            StackTraceElement caller = stackTraceElements[callerElementIndex];
            while (caller.getClassName().equals(stackTraceElements[1].getClassName()) &&
                    caller.getMethodName().equals(stackTraceElements[1].getMethodName())) {
                caller = stackTraceElements[++callerElementIndex];
            }
            Class<?> targetClass = ClassLoader.getSystemClassLoader().loadClass(caller.getClassName());
            return ArgumentsRunner.run(targetClass, object, args);
        } catch (ClassNotFoundException e) {
            throw new DeveloperRunnerException(e);
        }
    }

    /**
     * 构造一个参数运行器
     * @param runClass 运行类对象
     * @throws RunnerException 当检查Class发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public ArgumentsRunner(Class<?> runClass) {
        this(runClass, null);
    }

    /**
     * 构造一个参数运行器
     * @param runClass 运行类对象
     * @param config 运行器配置对象
     * @throws RunnerException 当检查Class发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public ArgumentsRunner(Class<?> runClass, ArgumentsRunnerConfig config) throws RunnerException {
        this.runClass = runClass;
        this.config = config == null ? new ArgumentsRunnerConfig() : config;
        commandMap = parseCommandMethodFromClass(runClass);
    }

    /**
     * 无实例对象启动运行器
     * @param args 运行参数
     * @return 返回命令处理方法的返回值.
     * @throws RunnerException 当运行器发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public Object run(String[] args) throws RunnerException {
        return run((Object) null, args);
    }

    /**
     * 启动运行器
     * @param object 构造时提供的class所属的实例对象
     * @param args 运行参数
     * @return 返回命令处理方法的返回值.
     * @throws RunnerException 当运行器发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public Object run(Object object, String[] args) throws RunnerException {
        if(object != null && !runClass.isInstance(object)) {
            throw new DeveloperRunnerException(
                    new IllegalArgumentException("The provided object is not an instance of runClass"));
        }

        Method targetMethod;
        if(Objects.requireNonNull(args).length == 0) {
            if(!commandMap.hasDefaultMethod()) {
                throw new CommandNotSpecifiedException();
            } else {
                targetMethod = commandMap.getDefaultMethod();
                object = null;
            }
        } else {
            String commandName = args[0];
            if(config.isCommandIgnoreCase()) {
                commandName = commandName.toLowerCase();
            }

            String command = object == null ? "Static." + commandName : commandName;
            if (!commandMap.containsKey(command)) {
                if(!commandMap.hasDefaultMethod()) {
                    throw new NoSuchCommandException(command);
                } else {
                    targetMethod = commandMap.getDefaultMethod();
                }
            } else {
                targetMethod = commandMap.get(command);
            }

            int modifiers = targetMethod.getModifiers();
            if(!Modifier.isPublic(modifiers)) {
                throw new IllegalModifierException("Method is not public: " + targetMethod.getName());
            } else if (Modifier.isAbstract(modifiers)) {
                throw new IllegalModifierException("Method is abstract: " + targetMethod.getName());
            }
        }

        String[] arguments = args.length <= 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        List<Object> paramList = generateParamListByFlag(targetMethod, arguments);
        Object[] params = new Object[targetMethod.getParameterTypes().length];
        paramList.toArray(params);
        try {
            return targetMethod.invoke(object, params);
        } catch (IllegalAccessException e) {
            throw new DeveloperRunnerException(e);
        } catch (InvocationTargetException e) {
            throw new DeveloperRunnerException(e.getTargetException());
        }

    }

    /**
     * 获取已获得的命令名. <br/>
     * 命令根据方法静态与否, 在命令名前有静态表示的 "Static." 前缀, <br/>
     * 例如实例命令方法 "Command",
     * 如果是静态方法, 则为 "Static.Command"
     * @return 返回存储命令名的Set对象, 如尚未解析Class内的方法, 则返回null
     */
    public Set<String> getCommandNames() {
        return commandMap == null ? null : new HashSet<>(commandMap.keySet());
    }

    /**
     * 构造参数列表
     * @param method 目标执行方法
     * @param args 字符串参数数组
     * @return 参数列表
     */
    private List<Object> generateParamListByFlag(Method method, String[] args) {
        ArgumentsProperties argsProp = new ArgumentsProperties(args);
        Parameter[] parameterTypes = method.getParameters();
        ArrayList<Object> paramList = new ArrayList<>(parameterTypes.length);
        int paramIndex = -1;
        for(Parameter paramType : parameterTypes) {
            paramIndex++;
            Argument argumentAnnotation = paramType.getAnnotation(Argument.class);
            if (argumentAnnotation == null) {
                throw new InvalidParameterException(
                        "Parameter in method " + method.getName() +
                                " without argument annotation (Index: " + paramIndex + ")");
            }
            if(!checkParametersType(paramType.getType())) {
                throw new InvalidParameterException(
                        "Method <" + method.getName() + "> Parameter has an unsupported type: " +
                                paramType.getType() + " (Index: " + paramIndex + ")");
            }

            String paramName = argumentAnnotation.name();
            if(Strings.isNullOrEmpty(paramName)) {
                if (paramType.isNamePresent()) {
                    paramName = paramType.getName();
                } else {
                    throw new InvalidParameterException("Parameter name is empty. (MethodName: " +
                            method.getName() + ", Index: " + paramIndex + ")");
                }

            }

            String typeName = paramType.getType().getTypeName();
            StringParameterParser<?> parameterParser = config.getStringParameterParser(paramType.getType());

            String paramValue;
            if(argsProp.containsKey(paramName) || !argumentAnnotation.force()) {
                paramValue = argsProp.getValue(paramName);
            } else {
                throw new ParameterNoFoundException(method.getName(), paramIndex, paramName);
            }

            if(Strings.isNullOrEmpty(paramValue) &&
                    !typeName.equals("boolean") && !typeName.equals(Boolean.class.getTypeName())) {
                if (!argumentAnnotation.force()) {
                    String defaultValue = argumentAnnotation.defaultValue();
                    if (!Strings.isNullOrEmpty(defaultValue)) {
                        paramValue = defaultValue;
                    } else {
                        if(config.isStrictDefaultCheck()) {
                            throw new InvalidParameterException(
                                    "Parameter force is false but has no default value. (Index: " + paramIndex + ")");
                        } else {
                            if(config.hasStringParameterParser(paramType.getType())) {
                                paramList.add(parameterParser.defaultValue());
                            } else {
                                paramList.add(Defaults.defaultValue(paramType.getType()));
                            }
                            continue;
                        }
                    }
                } else {
                    throw new ParameterNoFoundException(method.getName(), paramIndex, paramName);
                }
            }

            // 首先进行Boolean判断
            if ((typeName.equals("boolean") || typeName.equals(Boolean.class.getTypeName()))
                    && !config.hasStringParameterParser(Boolean.class)) {
                if(paramValue == null) {
                    paramList.add(Boolean.FALSE);
                } else if(paramValue.isEmpty()) {
                    paramList.add(Boolean.TRUE);
                } else {
                    paramValue = paramValue.trim().toLowerCase();
                    if(config.getTrueFlag().contains(paramValue)) {
                        paramList.add(Boolean.TRUE);
                    } else {
                        paramList.add(Boolean.FALSE);
                    }
                }
            } else if(typeName.equals(String.class.getTypeName()) && !config.hasStringParameterParser(String.class)) {
                // 如果类型为String且没有StringParameterParser, 则直接提供原始参数值
                paramList.add(paramValue);
            } else {
                // 使用StringParameterParser对参数进行转换
                try {
                    paramList.add(parameterParser.parse(paramValue));
                } catch(Throwable e) {
                    if(config.isUseDefaultValueInsteadOfException()) {
                        paramList.add(parameterParser.defaultValue());
                    } else {
                        throw new ParseParameterException(paramName, paramValue, typeName, e);
                    }
                }
            }
        }
        return paramList;
    }

    /**
     * 从指定class解析并获取命令处理方法
     * @param clazz 待解析的class对象
     * @return 存放命令与方法对应关系的Map
     * @throws RunnerException 当解析出异常时抛出
     */
    private CommandMap parseCommandMethodFromClass(Class<?> clazz) throws RunnerException {
        int classModifier = clazz.getModifiers();
        if(!Modifier.isPublic(classModifier)) {
            throw new IllegalModifierException("Class is not public");
        }else if(Modifier.isAbstract(classModifier) || Modifier.isInterface(classModifier)) {
            throw new IllegalModifierException("Class is abstract or interface");
        }

        Method[] methods = clazz.getDeclaredMethods();
        CommandMap commandMethodMap = new CommandMap(methods.length);
        for(Method method : methods) {
            if(isMainMethod(method)) {
                continue;
            }

            Command commandAnnotation = method.getDeclaredAnnotation(Command.class);
            if(commandAnnotation == null) {
                continue;
            }

            int modifiers = method.getModifiers();
            if(!Modifier.isPublic(modifiers)) {
                throw new IllegalModifierException("Method is not public: " + method.getName());
            } else if (Modifier.isAbstract(modifiers)) {
                throw new IllegalModifierException("Method is abstract: " + method.getName());
            }

            String commandName = commandAnnotation.commandName();
            if(Strings.isNullOrEmpty(commandName)) {
                commandName = method.getName();
            }

            if (!COMMAND_NAME_CHECK_PATTERN.matcher(commandName).matches()) {
                throw new IllegalCommandException("Illegal command name: " + commandName);
            }

            // 设为默认方法需满足的条件: 该类中唯一的默认方法, 注解标记了defaultValue, 参数列表为空, 方法是静态方法
            if(!commandMethodMap.hasDefaultMethod() && commandAnnotation.defaultCommand() &&
                    method.getAnnotatedParameterTypes().length == 0 && Modifier.isStatic(modifiers)
            ) {
                commandMethodMap.setDefaultMethod(method);
            }
            if(commandMethodMap.containsKey(commandName)) {
                throw new IllegalCommandException("There are multiple methods for the same command: " + commandName);
            } else if(commandMethodMap.containsValue(method)) {
                throw new IllegalCommandException("Multiple command names exist in the same method: " + commandName);
            }

            if(config.isCommandIgnoreCase()) {
                commandName = commandName.toLowerCase();
            }

            commandMethodMap.put(Modifier.isStatic(modifiers) ? "Static." + commandName : commandName, method);
        }

        return commandMethodMap;
    }

    /**
     * 检查是否为主方法.
     * @param method 待检查的方法.
     * @return 如果返回true, 则方法为main主方法.
     */
    private static boolean isMainMethod(Method method) {
        // public static void main(java.lang.String[])
        int modifier = method.getModifiers();
        if(Modifier.isPublic(modifier) && Modifier.isStatic( modifier) && method.getName().equals("main")) {
            if (method.getReturnType().isAssignableFrom(Void.TYPE)) {
                Class<?>[] typeClass;
                return (typeClass = method.getParameterTypes()).length == 1 &&
                        typeClass[0].isAssignableFrom(String[].class);
            }
        }
        return false;
    }

    /**
     * 检查类型是否支持
     * @param type 欲进行检查的Type类型
     * @return 支持则返回true
     */
    private boolean checkParametersType(Type type){
        String typeName = type.getTypeName();
        return  config.hasStringParameterParser(type) ||
                (typeName.equals("boolean") || typeName.equals(Boolean.class.getTypeName())) ||
                type.getTypeName().equals(String.class.getTypeName());
    }

    /*
        TODO(LamGC, 2020.04.08) - Plan:
            - 支持可选的对象注入, 而不是只能传入可转换参数 (参考Spring参数)
            - 仅限制特殊符号的使用而不限制语言字符
     */

}
