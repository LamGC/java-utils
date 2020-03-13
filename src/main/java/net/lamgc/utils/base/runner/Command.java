package net.lamgc.utils.base.runner;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 命令方法注解.
 * 标识了该注解的方法将视为一个命令方法.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    /**
     * 命令名.<br/>
     * 如果填空, 则使用方法名.
     * @return 方法所属的命令名
     */
    String commandName() default "";

    /**
     * 是否为默认方法.<br/>
     * 默认方法需要没有参数, 否则该参数将会被忽略.
     * @return 如果为默认方法则为true
     */
    boolean defaultCommand() default false;

}
