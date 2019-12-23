package net.lamgc.utils.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事件执行器.
 */
public class EventExecutor {

    private final EventHandlerList eventHandlerList = new BasicEventHandlerList();

    private final EventHandlerObjectMap eventHandlerObjectMap = new HashHandlerObjectMap();

    private final ThreadPoolExecutor threadPoolExecutor;

    /**
     * 构造一个EventExecutor
     * @param threadPoolExecutor 设置线程池, 线程池将用于执行Handler中的EventMethod.
     */
    public EventExecutor(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }

    /**
     * 添加EventHandler
     * @param handler EventHandler对象
     * @throws IllegalAccessException 当EventHandler内方法不为Public时可能会抛出,
     *                                是否抛出由{@link EventHandlerList}所使用的实现决定
     */
    public void addHandler(EventHandler handler) throws IllegalAccessException {
        eventHandlerObjectMap.addHandlerObject(handler);
        eventHandlerList.addEventHandler(handler.getClass());
    }

    /**
     * 投递事件.
     * 事件将会以方法为单位执行.
     * @param eventObject 带有事件参数的事件对象
     */
    public void executor(final EventObject eventObject){
        Set<Method> eventHandlerMethod = eventHandlerList.getEventHandlerMethod(eventObject.getClass());
        if(eventHandlerMethod == null){
            return;
        }
        eventHandlerMethod.forEach(method -> {
            EventHandler handler = eventHandlerObjectMap.getHandlerObject(method.getDeclaringClass());
            threadPoolExecutor.execute(() -> {
                try {
                    method.invoke(handler, eventObject);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * 等待任务线程池内的任务执行完成.
     * @param timeout 时间
     * @param unit 时间单位
     * @throws InterruptedException 当发生死锁时抛出
     */
    public void awaitEventExecutor(long timeout, TimeUnit unit) throws InterruptedException {
        threadPoolExecutor.awaitTermination(timeout, unit);
    }

}
