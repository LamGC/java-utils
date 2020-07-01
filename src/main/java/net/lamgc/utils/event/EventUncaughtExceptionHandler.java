package net.lamgc.utils.event;

import java.lang.reflect.Method;

/**
 * 事件异常处理接口.
 * <p>相比于{@link Thread.UncaughtExceptionHandler}能够接收更多有关事件异常的信息, 建议使用本处理接口.</p>
 */
@FunctionalInterface
public interface EventUncaughtExceptionHandler{

    /**
     * 当事件对象抛出异常时触发.
     * @param executeThread 执行事件方法的线程对象
     * @param handler 事件处理方法所在{@link EventHandler}
     * @param handlerMethod 抛出异常的方法对象
     * @param event 事件对象
     * @param cause 异常对象
     */
    void exceptionHandler(
            Thread executeThread,
            EventHandler handler,
            Method handlerMethod,
            EventObject event,
            Throwable cause);
}