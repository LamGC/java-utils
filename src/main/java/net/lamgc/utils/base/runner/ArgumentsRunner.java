package net.lamgc.utils.base.runner;

import com.google.common.base.Strings;
import net.lamgc.utils.base.ArgumentsProperties;
import net.lamgc.utils.base.StringParser;
import net.lamgc.utils.base.runner.exception.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Pattern;

public class ArgumentsRunner {

    private CommandMap commandMap;

    /**
     * 无配置启动一个运行器
     * @param runClass 待运行的Class对象
     * @param args 运行参数
     * @return 返回命令处理方法的返回值.
     * @throws RunnerException 当运行器发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public static Object run(Class<?> runClass, String[] args) throws RunnerException {
        return new ArgumentsRunner(runClass).run(args);
    }

    /**
     * 在调用该方法所在类启动ArgumentsRunner
     * @param args 参数
     * @return 返回命令处理方法的返回值.
     * @throws RunnerException 当运行器发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public static Object runInThisClass(String[] args) throws RunnerException {
        try {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            if(stackTraceElements.length < 3) {
                throw new RunnerException(RunnerException.TRIGGER_DEVELOPER, new IllegalStateException("Stack error"));
            }
            Class<?> targetClass = ClassLoader.getSystemClassLoader().loadClass(stackTraceElements[2].getClassName());
            return ArgumentsRunner.run(targetClass, args);
        } catch (ClassNotFoundException e) {
            throw new RunnerException(RunnerException.TRIGGER_DEVELOPER, e);
        }
    }

    /**
     * 构造一个参数运行器
     * @param runClass 运行类对象
     * @throws RunnerException 当检查Class发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public ArgumentsRunner(Class<?> runClass) throws RunnerException {
        commandMap = parseCommandMethodFromClass(runClass);
    }

    /**
     * 启动运行器
     * @param args 运行参数
     * @return 返回命令处理方法的返回值.
     * @throws RunnerException 当运行器发生异常时抛出,
     *                          注意检查 {@linkplain RunnerException#getExceptionTrigger()} 返回值,
     *                          该异常会标记引发原因, 详情请查看{@link RunnerException#getExceptionTrigger()}
     */
    public Object run(String[] args) throws RunnerException {
        Method targetMethod;
        if(Objects.requireNonNull(args).length == 0) {
            if(!commandMap.hasDefaultMethod()) {
                throw new RunnerException(RunnerException.TRIGGER_USER, "Arguments is empty");
            } else {
                targetMethod = commandMap.getDefaultMethod();
            }
        } else {
            String command = args[0];
            if (!commandMap.containsKey(command)) {
                throw new NoSuchCommandException(command);
            }
            targetMethod = commandMap.get(command);
            int modifiers = targetMethod.getModifiers();
            if(!Modifier.isPublic(modifiers)) {
                throw new IllegalModifierException("Method is not public: " + targetMethod.getName());
            } else if (Modifier.isAbstract(modifiers)) {
                throw new IllegalModifierException("Method is abstract: " + targetMethod.getName());
            }
        }

        String[] arguments = args.length <= 1 ? new String[0] : Arrays.copyOfRange(args, 1, args.length);
        List<Object> paramList = generateParamListByFlag(targetMethod, arguments);
        try {
            return targetMethod.invoke(null, paramList.toArray(new Object[0]));
        } catch (IllegalAccessException e) {
            throw new RunnerException(RunnerException.TRIGGER_DEVELOPER, e);
        } catch (InvocationTargetException e) {
            throw new RunnerException(RunnerException.TRIGGER_DEVELOPER, e.getTargetException());
        }

    }

    /**
     * 构造参数列表
     * @param method 目标执行方法
     * @param args 字符串参数数组
     * @return 参数列表
     */
    private List<Object> generateParamListByFlag(Method method, String[] args) {
        ArgumentsProperties argsProp = new ArgumentsProperties(args);
        //AnnotatedType[] parameterTypes = method.getAnnotatedParameterTypes();
        Parameter[] parameterTypes = method.getParameters();
        ArrayList<Object> paramList = new ArrayList<>(parameterTypes.length);
        int paramIndex = -1;
        for(Parameter paramType : parameterTypes) {
            paramIndex++;
            Argument argumentAnnotation = paramType.getAnnotation(Argument.class);
            if (argumentAnnotation == null) {
                throw new InvalidParameterException("Parameter in method " + method.getName() + " without argument annotation (Index: " + paramIndex + ")");
            }
            if(!checkParametersType(paramType.getType().getTypeName())) {
                throw new InvalidParameterException("Method <" + method.getName() + "> Parameter has an unsupported type: " + paramType.getType() + " (Index: " + paramIndex + ")");
            }

            String paramName = argumentAnnotation.name();
            if(Strings.isNullOrEmpty(paramName)) {
                if (paramType.isNamePresent()) {
                    paramName = paramType.getName();
                } else {
                    throw new InvalidParameterException("Parameter name is empty. (MethodName: " + method.getName() + ", Index: " + paramIndex + ")");
                }

            }

            String typeName = paramType.getType().getTypeName();

            String paramValue;
            if(argsProp.containsKey(paramName)) {
                paramValue = argsProp.getValue(paramName);
            } else {
                throw new ParameterNoFoundException(method.getName(), paramIndex, paramName);
            }

            if(Strings.isNullOrEmpty(paramValue)) {
                if (!argumentAnnotation.force()) {
                    String defaultValue = argumentAnnotation.defaultValue();
                    if (!Strings.isNullOrEmpty(defaultValue)) {
                        paramValue = defaultValue;
                    } else {
                        throw new InvalidParameterException("Parameter force is false but has no default value. (Index: " + paramIndex + ")");
                    }
                } else if(typeName.toLowerCase().lastIndexOf("boolean") == -1) {
                    throw new ParameterNoFoundException(method.getName(), paramIndex, paramName);
                }
            }

            if (typeName.toLowerCase().lastIndexOf("boolean") != -1) {
                if(paramValue.isEmpty()) {
                    paramList.add(Boolean.TRUE);
                } else {
                    paramValue = paramValue.trim();
                    if("true t yes y".contains(paramValue.toLowerCase())) {
                        paramList.add(Boolean.TRUE);
                    } else {
                        paramList.add(Boolean.FALSE);
                    }
                }
            } else {
                try {
                    StringParser parser = StringParser.getObjectTypeParser(paramType.getType().getTypeName());
                    paramList.add(parser.parser.parse(paramValue));
                } catch(Throwable e) {
                    throw new ParseParameterException(paramName, paramValue, typeName, e);
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
    public static CommandMap parseCommandMethodFromClass(Class<?> clazz) throws RunnerException {
        int classModifier = clazz.getModifiers();
        if(!Modifier.isPublic(classModifier)) {
            throw new IllegalModifierException("Class is not public");
        }else if(Modifier.isAbstract(classModifier) || Modifier.isInterface(classModifier)) {
            throw new IllegalModifierException("Class is abstract or interface");
        }

        Method[] methods = clazz.getDeclaredMethods();
        CommandMap commandMethodMap = new CommandMap(methods.length);
        Pattern flagCheckPattern = Pattern.compile("^[A-Za-z_$]+[A-Za-z0-9_\\-$]+$");
        for(Method method : methods) {
            int modifiers = method.getModifiers();
            if(!Modifier.isStatic(modifiers)) {
                continue;
            } else if(isMainMethod(method)) {
                continue;
            }

            Command commandAnnotation = method.getDeclaredAnnotation(Command.class);
            if(commandAnnotation == null) {
                continue;
            }

            String commandName = commandAnnotation.commandName();
            if(Strings.isNullOrEmpty(commandName)) {
                commandName = method.getName();
            }



            // 检查命令是否合法
                /*
                需要检查的情况:
                    空命令
                    带空格的非法命令
                    非法字符?
                可行的方案:
                    限制命令与Java标识符命名规范一致
                        如果采用该方案，则Command命令连value都能不要, 直接方法名完事了
                    (必选) 正则表达式检查命名
                 */
            if (!flagCheckPattern.matcher(commandName).matches()) {
                throw new IllegalCommandException("Illegal command name: " + commandName);
            }

            if(!commandMethodMap.hasDefaultMethod() && commandAnnotation.defaultCommand() &&
                    method.getAnnotatedParameterTypes().length == 0
            ) {
                commandMethodMap.setDefaultMethod(method);
            }
            if(commandMethodMap.containsKey(commandName)) {
                throw new IllegalCommandException("There are multiple methods for the same command: " + commandName);
            } else if(commandMethodMap.containsValue(method)) {
                throw new IllegalCommandException("Multiple command names exist in the same method: " + commandName);
            }

            commandMethodMap.put(commandName, method);
        }

        return commandMethodMap;
    }

    private static boolean isMainMethod(Method method) {
        // public static void main(java.lang.String[])
        int modifier = method.getModifiers();
        if(Modifier.isPublic(modifier) && Modifier.isStatic( modifier) && method.getName().equals("main")) {
            if (method.getReturnType().isAssignableFrom(Void.TYPE)) {
                Class<?>[] typeClass;
                return (typeClass = method.getParameterTypes()).length == 1 && typeClass[0].isAssignableFrom(String[].class);
            }
        }
        return false;
    }

    /**
     * 检查类型是否支持
     * @param typeName 类型名称
     * @return 支持则返回true
     */
    private boolean checkParametersType(String typeName){
        HashSet<String> typeSet = new HashSet<>(Arrays.asList(
                "int","long","short","float","double","boolean",

                "java.lang.Integer","java.lang.Long","java.lang.Short",
                "java.lang.Float","java.lang.Double","java.lang.Boolean",
                "java.lang.String"));
        return typeSet.contains(typeName);
    }

}
