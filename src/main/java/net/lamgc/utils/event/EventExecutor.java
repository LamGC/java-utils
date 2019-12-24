package net.lamgc.utils.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 事件执行器.
 */
public class EventExecutor {

    private final EventHandlerList eventHandlerList;

    private final EventHandlerObjectMap eventHandlerObjectMap;

    private final ThreadPoolExecutor threadPoolExecutor;

    private Thread.UncaughtExceptionHandler exceptionHandler = null;

    /**
     * 构造一个EventExecutor.
     * @param threadPoolExecutor 事件线程池, 线程池将用于执行Handler中的EventMethod.
     */
    public EventExecutor(ThreadPoolExecutor threadPoolExecutor){
        this(threadPoolExecutor, null, null);
    }

    /**
     * 构造一个EventExecutor.
     * @param threadPoolExecutor 事件线程池, 线程池将用于执行Handler中的EventMethod.
     * @param eventHandlerList 事件列表, 用于存储EventObject对应EventHandlerMethod.
     * @param eventHandlerObjectMap EventHandlerObject存储, 用于存储EventHandler对象.
     */
    public EventExecutor(ThreadPoolExecutor threadPoolExecutor,
                         EventHandlerList eventHandlerList,
                         EventHandlerObjectMap eventHandlerObjectMap) {
        this.threadPoolExecutor = threadPoolExecutor;
        final ThreadFactory threadFactory = this.threadPoolExecutor.getThreadFactory();
        this.threadPoolExecutor.setThreadFactory(r -> {
            Thread newThread = threadFactory.newThread(r);
            if(newThread.getUncaughtExceptionHandler() == newThread.getThreadGroup()){
                newThread.setUncaughtExceptionHandler((t, e) -> {
                    if(this.exceptionHandler != null){
                        this.exceptionHandler.uncaughtException(t, e);
                    }
                });
            }
            return newThread;
        });
        this.eventHandlerList = eventHandlerList != null ? eventHandlerList : new BasicEventHandlerList();
        this.eventHandlerObjectMap = eventHandlerObjectMap != null ? eventHandlerObjectMap : new HashHandlerObjectMap();
    }

    /**
     * 添加EventHandler.
     * @param handler EventHandler对象
     * @throws IllegalAccessException 当EventHandler内方法不为Public时可能会抛出,
     *                                是否抛出由{@link EventHandlerList}所使用的实现决定
     */
    public void addHandler(EventHandler handler) throws IllegalAccessException {
        eventHandlerObjectMap.addHandlerObject(handler);
        eventHandlerList.addEventHandler(handler.getClass());
    }

    /**
     * 删除EventHandler
     * @param handler 要删除的EventHandler对象
     */
    public void removeHandler(EventHandler handler){
        eventHandlerObjectMap.removeHandlerObject(handler);
        eventHandlerList.removeEventHandler(handler);
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
            final Set<EventHandler> handlerSet = eventHandlerObjectMap.getHandlerObject(method.getDeclaringClass());
            threadPoolExecutor.execute(() -> handlerSet.forEach(handler -> {
                try {
                    method.invoke(handler, eventObject);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e){
                    throw new RuntimeException(e.getCause() == null ? e : e.getCause());
                }
            }));
        });
    }

    /**
     * 对指定{@link EventHandler}投递事件
     * @param handler 要进行事件投递的EventHandler
     * @param eventObject 事件对象
     * @return 返回已处理事件方法数量
     */
    public int executor(EventHandler handler, EventObject eventObject){
        Method[] methods = handler.getClass().getDeclaredMethods();
        int invokeCount = 0;
        for (Method method : methods) {
            int modifiers = method.getModifiers();
            if(!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)){
                continue;
            }
            Class<?>[] types = method.getParameterTypes();
            if(types.length != 1){
                continue;
            }
            if(!eventObject.getClass().isAssignableFrom(types[0])){
                continue;
            }

            threadPoolExecutor.execute(() -> {
                try {
                    method.invoke(handler, eventObject);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e){
                    throw new RuntimeException(e.getCause() == null ? e : e.getCause());
                }
            });
            invokeCount++;
        }
        return invokeCount;
    }

    /**
     * 等待线程池内任务执行完成并关闭线程池.
     * @param timeout 时间
     * @param unit 时间单位
     * @return 如果在设置时间内线程池任务执行完成并关闭返回true, 否则返回false.
     * @throws InterruptedException 当发生中断时抛出
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return threadPoolExecutor.awaitTermination(timeout, unit);
    }

    /**
     * 设置线程池异常处理类.
     * EventExecutor在内部经过处理,
     * 可以动态更改UncaughtExceptionHandler而不用担心设置后需要等线程重新建立后才生效.
     * @param handler 处理类对象
     */
    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler){
        this.exceptionHandler = handler;
    }

    /**
     * 获取EventExecutor所使用的{@link ThreadPoolExecutor}
     * @return ThreadPoolExecutor对象
     */
    public ThreadPoolExecutor getThreadPoolExecutor(){
        return this.threadPoolExecutor;
    }

    /**
     * 获取设定的UncaughtExceptionHandler
     * @return 返回设置的UncaughtExceptionHandler, 如无设置则返回null
     */
    public Thread.UncaughtExceptionHandler getExceptionHandler(){
        return this.exceptionHandler;
    }

    /**
     * 关闭执行器.
     * @param shutdownNow 是否立刻关闭
     * @return 如果 {@code shutdownNow}为 {@code true}, 则返回线程池内未完成的事件任务.
     */
    public List<Runnable> shutdown(boolean shutdownNow){
        if (shutdownNow) {
            return threadPoolExecutor.shutdownNow();
        } else {
            threadPoolExecutor.shutdown();
            return null;
        }
    }

    @Override
    protected void finalize() {
        threadPoolExecutor.shutdownNow();
    }
}
