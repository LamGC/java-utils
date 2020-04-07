package net.lamgc.utils.base.runner;

import net.lamgc.utils.base.BasicTypeConverter;
import net.lamgc.utils.base.runner.exception.IllegalModifierException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Hashtable;

public class StringParameterParserMap {

    private final Hashtable<Type, StringParameterParser<?>> parserMap = new Hashtable<>();

    /**
     * 添加一个Parser对象.
     * @param parser 欲添加的Parser对象.
     * @throws IllegalModifierException 当class为abstract时抛出.
     * @throws RuntimeException
     *          当获取不到{@linkplain StringParameterParser#parse(String) StringParameterParser.parser(String)}方法时抛出{@link NoSuchMethodException}异常.
     */
    public void addParser(StringParameterParser<?> parser) {
        for (Type genericInterface : parser.getClass().getGenericInterfaces()) {
            if(genericInterface instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                if (StringParameterParser.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
                    parserMap.put(parameterizedType.getActualTypeArguments()[0], parser);
                }
            }
        }
    }

    /**
     * 删除一个Parser.
     * @param type 要删除的parser对应Type.
     * @return 详见 {@link java.util.Map#remove(Object)}
     */
    public StringParameterParser<?> removeParser(Type type) {
        return parserMap.remove(BasicTypeConverter.valueOfBasicTypeName(type));
    }

    /**
     * 根据Type获取Parser.
     * @param type 指定的Type对象.
     * @return 如果存在, 返回Parser对象, 否则返回null.
     */
    public StringParameterParser<?> getParser(Type type) {
        return parserMap.get(BasicTypeConverter.valueOfBasicTypeName(type));
    }

    /**
     * 指定Type是否存在Parser.
     * @param type 要检查的Type对象.
     * @return 如果存在, 返回true.
     */
    public boolean hasParser(Type type) {
        return parserMap.containsKey(BasicTypeConverter.valueOfBasicTypeName(type));
    }


}
