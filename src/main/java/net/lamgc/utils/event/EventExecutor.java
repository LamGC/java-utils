package net.lamgc.utils.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final AtomicBoolean enableEventResend = new AtomicBoolean();

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
     * @throws InterruptedException 当等待执行完成时发生中断, 则抛出异常.
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
     * @throws IllegalAccessException 当EventHandler访问权不为public时抛出异常.
     */
    public int executor(EventHandler handler, EventObject eventObject) throws IllegalAccessException {
        Class<?> handlerClass = handler.getClass();
        int classModifier = handlerClass.getModifiers();
        if(!Modifier.isPublic(classModifier)) {
            throw new IllegalAccessException("class is not public");
        }

        Method[] methods = handlerClass.getDeclaredMethods();
        int invokeCount = 0;
        for (Method method : methods) {
            if(!checkMethod(method, eventObject)) {
                continue;
            }
            
            executeEvent(handler, eventObject, method, null);
            invokeCount++;
        }
        return invokeCount;
    }

    /**
     * 设置事件重投是否启用.<br>
     * 事件重新投递(事件重投, EventResend)可以将当前EventHandler的方法处理的事件再次投递给当前EventHandler再次进行处理,
     * 通过事件重投, 可以做到例如让事件延迟处理的能力.
     * @param enable 如果启用,
     *               则EventHandler内的事件处理方法将可以调用{@link #resendCurrentEvent()}重新投递当前任务到该EventHandler.
     */
    public void setEnableEventResend(boolean enable) {
        enableEventResend.set(enable);
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
            threadPoolExecutor.execute(() -> {
                if(enableEventResend.get()) {
                    setThreadResendInfo(this, handler, event);
                }
                createEventTask(handler, event, eventMethod).run();
                clearThreadResendInfo();
            });
        } else {
            executeCount.incrementAndGet();
            threadPoolExecutor.execute(() -> {
                try {
                    if(enableEventResend.get()) {
                        setThreadResendInfo(this, handler, event);
                    }
                    createEventTask(handler, event, eventMethod).run();
                } finally {
                    if(enableEventResend.get()) {
                        clearThreadResendInfo();
                    }
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

    private final static ThreadLocal<EventExecutor> threadEventExecutor = new ThreadLocal<>();
    private final static ThreadLocal<EventHandler> threadEventHandler = new ThreadLocal<>();
    private final static ThreadLocal<EventObject> threadEventObject = new ThreadLocal<>();

    /**
     * 设置当前线程的事件重投信息
     * @param executor 当前事件执行器
     * @param currentHandler 当前EventHandler对象
     * @param currentObject 当前EventObject对象
     */
    private static void setThreadResendInfo(EventExecutor executor, EventHandler currentHandler, EventObject currentObject) {
        threadEventExecutor.set(Objects.requireNonNull(executor));
        threadEventHandler.set(Objects.requireNonNull(currentHandler));
        threadEventObject.set(Objects.requireNonNull(currentObject));
    }

    /**
     * 清除事件重投信息.
     */
    private static void clearThreadResendInfo() {
        threadEventExecutor.remove();
        threadEventHandler.remove();
        threadEventObject.remove();
    }

    /**
     * 重新投递当前任务到当前Handler.<br>
     * 该操作不会将任务投递到其他Handler.<br>
     * 注意: 该方法本身并不会阻止多次调用, 故事件处理方法需自行控制重新投递操作.<br>
     */
    public static void resendCurrentEvent() {
        EventExecutor executor = threadEventExecutor.get();
        if(executor == null) {
            throw new UnsupportedOperationException("Resend not enabled");
        }

        try {
            executor.executor(
                    Objects.requireNonNull(threadEventHandler.get()), Objects.requireNonNull(threadEventObject.get()));
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 检查指定方法是否为合法的事件接收方法
     * @param method 待检查的方法
     * @return 如果是合法的事件接收方法, 返回true
     */
    public static boolean checkMethod(Method method) {
        return checkMethod(method, null);
    }

    /**
     * 检查指定方法是否为合法的事件接收方法
     * @param method 待检查的方法
     * @param eventObject 指定的EventObject, 如没有指定可为null, 指定后, 将会检查继承性
     * @return 如果是合法的事件接收方法, 返回true
     */
    private static boolean checkMethod(Method method, EventObject eventObject) {
        int methodModifiers = method.getModifiers();
        if(!Modifier.isPublic(methodModifiers)){
            return false;
        }
        Class<?>[] types = method.getParameterTypes();
        if(types.length != 1){
            return false;
        }
        if(eventObject == null) {
            if(!EventObject.class.isAssignableFrom(types[0])) {
                return false;
            }
        } else {
            if(!types[0].isAssignableFrom(eventObject.getClass())) {
                return false;
            }
        }

        return method.getDeclaredAnnotation(NotAccepted.class) == null;
    }
    
    @Override
    protected void finalize() {
        threadPoolExecutor.shutdownNow();
    }
}
