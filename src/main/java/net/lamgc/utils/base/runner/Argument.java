package net.lamgc.utils.base.runner;

import java.lang.annotation.*;

/**
 * 参数注解.
 * 该注解需要与方法参数数量一致.
 * {@link ArgumentsRunner} 将根据对应参数位置的类型转换参数值.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Argument {

    /**
     * 参数名.
     * 如果需要省略参数名, 使用方法名, 则需要在编译时指定 "{@code -parameters}"参数以保留参数名
     * @return 返回参数标识名
     */
    String name() default "";

    /**
     * 是否必填.
     *
     * 如果必填为true且参数不存在则抛出异常.
     * 如果必填为false, 如已设置defaultValue则转换defaultValue并传参;
     * 如未设置defaultValue, 如参数类型为基本数据类型则传入该类型默认值,
     * 如参数类型为对象则填入null.
     * @return 如果参数必须存在, 则返回true
     */
    boolean force() default true;

    /**
     * 默认值, 当{@code force = false}时有效
     * @return 返回参数缺省值
     */
    String defaultValue() default "";

}
