package net.lamgc.utils.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 事件执行器.
 */
public class EventExecutor {

    private final EventHandlerList eventHandlerList;

    private final EventHandlerObjectMap eventHandlerObjectMap;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final AtomicReference<Thread.UncaughtExceptionHandler> exceptionHandler = new AtomicReference<>();

    private final AtomicReference<EventUncaughtExceptionHandler> eventExceptionHandler = new AtomicReference<>();

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
                    EventUncaughtExceptionHandler eventUncaughtHandler;
                    if(e instanceof EventInvokeException && (eventUncaughtHandler = eventExceptionHandler.get()) != null){
                        EventInvokeException exception = (EventInvokeException) e;
                        eventUncaughtHandler.exceptionHandler(
                                /* Thread: */ t,
                                exception.getHandler(),
                                exception.getHandlerMethod(),
                                exception.getEventObject(),
                                exception.getCause());
                        return;
                    }
                    Thread.UncaughtExceptionHandler threadExceptionHandler;
                    if((threadExceptionHandler = this.exceptionHandler.get()) != null){
                        threadExceptionHandler.uncaughtException(t, e);
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
        if(!eventHandlerObjectMap.removeHandlerObject(handler)){
            eventHandlerList.removeEventHandler(handler);
        }
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
            threadPoolExecutor.execute(() ->
                    handlerSet.forEach(handler -> executeEvent(handler, eventObject, method, null)));
        });
    }

    /**
     * 同步投递事件.
     * 方法将会在事件执行结束后返回.
     * @param eventObject 需投递的事件对象
     */
    public void executorSync(final EventObject eventObject) throws InterruptedException {
        Set<Method> eventHandlerMethod = eventHandlerList.getEventHandlerMethod(eventObject.getClass());
        if(eventHandlerMethod == null){
            return;
        }

        final AtomicInteger executeCount = new AtomicInteger();

        eventHandlerMethod.forEach(method -> {
            final Set<EventHandler> handlerSet = eventHandlerObjectMap.getHandlerObject(method.getDeclaringClass());
            handlerSet.forEach(handler -> executeEvent(handler, eventObject, method, executeCount));
        });
        if(executeCount.get() != 0){
            synchronized (executeCount) {
                executeCount.wait();
            }
        }
    }


    /**
     * 对指定{@link EventHandler}投递事件
     * @param handler 要进行事件投递的EventHandler
     * @param eventObject 事件对象
     * @return 返回已处理事件方法数量
     */
    public int executor(EventHandler handler, EventObject eventObject) throws IllegalAccessException {
        Class<?> handlerClass = handler.getClass();
        int classModifier = handlerClass.getModifiers();
        if(!Modifier.isPublic(classModifier)) {
            throw new IllegalAccessException("class is not public");
        } else if(Modifier.isInterface(classModifier) || Modifier.isAbstract(classModifier)) {
            throw new IllegalStateException("Class is an interface or abstract");
        }

        Method[] methods = handlerClass.getDeclaredMethods();
        int invokeCount = 0;
        for (Method method : methods) {
            int methodModifiers = method.getModifiers();
            if(!Modifier.isPublic(methodModifiers)){
                continue;
            }
            Class<?>[] types = method.getParameterTypes();
            if(types.length != 1){
                continue;
            }
            if(!eventObject.getClass().isAssignableFrom(types[0])){
                continue;
            }

            executeEvent(handler, eventObject, method, null);
            invokeCount++;
        }
        return invokeCount;
    }

    /**
     * 对指定EventHandler的Method投递事件.
     * @param handler 要投递事件的EventHandler对象
     * @param event 事件对象
     * @param eventMethod 事件方法
     * @param executeCount 当需要进行执行计次时使用
     */
    private void executeEvent(final EventHandler handler,
                              final EventObject event,
                              final Method eventMethod,
                              final AtomicInteger executeCount){
        if(executeCount == null){
            threadPoolExecutor.execute(createEventTask(handler, event, eventMethod));
        } else {
            executeCount.incrementAndGet();
            threadPoolExecutor.execute(() -> {
                try {
                    createEventTask(handler, event, eventMethod).run();
                } finally {
                    int count = executeCount.decrementAndGet();
                    if (count == 0) {
                        synchronized (executeCount) {
                            executeCount.notifyAll();
                        }
                    }
                }
            });
        }
    }

    /**
     * 创建事件任务
     * @param handler 要投递事件的EventHandler对象
     * @param event 事件对象
     * @param eventMethod 事件方法
     */
    private Runnable createEventTask(EventHandler handler, EventObject event, Method eventMethod){
        return () -> {
            try {
                eventMethod.invoke(handler, event);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e){
                throw new EventInvokeException(handler, eventMethod, event, e.getCause());
            }
        };
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
     *
     * EventExecutor在内部经过处理,
     * 可以动态更改UncaughtExceptionHandler而不用担心设置后需要等线程重新建立后才生效.
     * 注意: 如需捕获EventHandler方法抛出的异常请使用{@link #setEventUncaughtExceptionHandler(EventUncaughtExceptionHandler)},
     * 设置捕获EventHandler抛出的异常, 因EventExecutor内部处理,
     * UncaughtExceptionHandler无法捕获{@link InvocationTargetException}异常来获取事件方法抛出的异常.
     * @param handler 处理类对象
     */
    public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler handler){
        this.exceptionHandler.set(handler);
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
        return this.exceptionHandler.get();
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

    /**
     * 设置事件异常捕获处理对象.
     * 该对象能详细获得
     * @param handler 事件异常捕获处理对象
     */
    public void setEventUncaughtExceptionHandler(EventUncaughtExceptionHandler handler){
        this.eventExceptionHandler.set(handler);
    }

    @Override
    protected void finalize() {
        threadPoolExecutor.shutdownNow();
    }
}
