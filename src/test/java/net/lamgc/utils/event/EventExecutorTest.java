package net.lamgc.utils.event;

import com.google.common.base.Throwables;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class EventExecutorTest {

    private final Logger log = LoggerFactory.getLogger(EventExecutorTest.class.getSimpleName());

    @Test
    public void executorTest() throws IllegalAccessException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        threadPoolExecutor.setRejectedExecutionHandler((r, executor) -> {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        AtomicInteger invokeCount = new AtomicInteger();
        EventExecutor executor = new EventExecutor(threadPoolExecutor);
        Assert.assertEquals(threadPoolExecutor, executor.getThreadPoolExecutor());

        SimpleEventHandler handler1 = new SimpleEventHandler("handler1");
        SimpleEventHandler handler2 = new SimpleEventHandler("handler2");
        executor.addHandler(handler1);
        executor.addHandler(handler2);
        executor.executor(new SimpleEventObject(1, "HelloWorld", invokeCount));
        Thread.sleep(500L);
        Assert.assertEquals(2, invokeCount.get());
        invokeCount.set(0);
        log.info("SingleHandlerTest-----");
        log.info("Execute Count: {}", executor.executor(handler2, new SimpleEventObject(1, "HelloWorld", invokeCount)));
        Thread.sleep(500L);
        Assert.assertEquals(1, invokeCount.get());
        invokeCount.set(0);
        executor.removeHandler(handler1);
        log.info("deleted handler1");
        executor.executor(new SimpleEventObject(2, "HelloWorld123", invokeCount));
        Thread.sleep(500L);
        Assert.assertEquals(1, invokeCount.get());
        invokeCount.set(0);
        executor.removeHandler(handler2);
        log.info("deleted handler2");

        new Thread(() -> {
            boolean flag = false;
            try {
                flag = executor.awaitTermination(2L, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {

            }
            log.info("Executor Shutdown.(Flag: {})", flag);
        }).start();

        executor.executor(new SimpleEventObject(2, "HelloWorld123", invokeCount));
        Thread.sleep(500L);
        executor.shutdown(false);
        Assert.assertEquals(0, invokeCount.get());
    }

    @Test
    public void syncExecuteTest() throws IllegalAccessException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        threadPoolExecutor.setRejectedExecutionHandler((r, executor) -> {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        AtomicInteger invokeCount = new AtomicInteger();
        EventExecutor executor = new EventExecutor(threadPoolExecutor);
        for (int i = 0; i < 100; i++) {
            executor.addHandler(new SimpleEventHandler("handler" + i));
        }
        log.info("[{}] execute event", System.currentTimeMillis());
        executor.executorSync(new SimpleEventObject(1, "HelloWorld", invokeCount));
        log.info("[{}] done", System.currentTimeMillis());
        System.gc();
    }

    @Test
    public void shutdownNowTest() throws IllegalAccessException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        threadPoolExecutor.setRejectedExecutionHandler((r, executor) -> {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        EventExecutor executor = new EventExecutor(threadPoolExecutor);

        for (int i = 0; i < 100; i++) {
            executor.addHandler(new SimpleEventHandler("handler" + i));
        }
        new Thread(() -> {
            try {
                Assert.assertTrue(executor.awaitTermination(100L, TimeUnit.MILLISECONDS));
            } catch (InterruptedException ignored) {
            }
        }).start();
        executor.shutdown(true);
    }

    @Test
    public void caughtExceptionTest() throws IllegalAccessException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        threadPoolExecutor.setRejectedExecutionHandler((r, executor) -> {
            try {
                executor.getQueue().put(r);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        EventExecutor executor = new EventExecutor(threadPoolExecutor);

        AtomicBoolean handlerException = new AtomicBoolean(false);
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
            log.error("Thread({}) uncaught exception", t.getName());
            e.printStackTrace();
        };
        executor.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        Assert.assertEquals(uncaughtExceptionHandler, executor.getExceptionHandler());

        executor.setEventUncaughtExceptionHandler((t, handler, handlerMethod, event, cause) -> {
            log.info("Thread: [{}] {}, Handler: {}, MethodName: {}, Event: {} - throw Exception: {}",
                    t.getId(),
                    t.getName(),
                    handler.toString(),
                    handlerMethod.getName(),
                    event.getClass().getSimpleName(),
                    cause.getMessage()
            );
            cause.printStackTrace();
            handlerException.set(true);
        });

        SimpleEventHandler handler = new SimpleEventHandler("handler1");
        executor.addHandler(handler);
        executor.executor(new ExceptionThrowEvent(new NullPointerException()));
        Thread.sleep(1000L);
        Assert.assertTrue(handlerException.get());
    }

    @Test
    public void threadUncaughtExceptionTest() throws IllegalAccessException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        EventExecutor executor = new EventExecutor(threadPoolExecutor);

        AtomicBoolean handlerException = new AtomicBoolean(false);
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
            log.error("Thread({}) uncaught exception", t.getName());
            e.printStackTrace();
            handlerException.set(true);
        };
        executor.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        Assert.assertEquals(uncaughtExceptionHandler, executor.getExceptionHandler());
        SimpleEventHandler handler = new SimpleEventHandler("handler1");
        executor.addHandler(handler);
        executor.executor(new ExceptionThrowEvent(new NullPointerException()));
        Thread.sleep(1000L);
        Assert.assertTrue(handlerException.get());
    }

    @Test(expected = IllegalStateException.class)
    public void abstractClassTest() throws IllegalAccessException {
        EventHandlerList list = new BasicEventHandlerList();
        list.addEventHandler(AbstractEventHandler.class);
    }

    @Test(expected = IllegalStateException.class)
    public void interfaceClassTest() throws IllegalAccessException {
        EventHandlerList list = new BasicEventHandlerList();
        list.addEventHandler(EventHandler.class);
    }

    @Test(expected = IllegalAccessException.class)
    public void protectedClassTest() throws IllegalAccessException {
        EventHandlerList list = new BasicEventHandlerList();
        list.addEventHandler(PrivateEventHandler.class);
    }

    @Test(expected = IllegalAccessException.class)
    public void protectedClassTestForExecuteHandler() throws IllegalAccessException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        EventExecutor executor = new EventExecutor(threadPoolExecutor);
        executor.executor(new PrivateEventHandler(), new SimpleEventObject(0, "test", null));
    }

    @Test
    public void eventResendTest() throws IllegalAccessException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        EventExecutor executor = new EventExecutor(threadPoolExecutor);
        executor.setEventUncaughtExceptionHandler((thread, handler, method, object, cause) ->
                log.error("事件执行失败.\n" +
                         "Thread: {}\nEventHandler: {}\n HandlerMethod: {}\nEventObject: {}\nThrowableStackTrace:\n{}",
                thread,
                handler,
                method,
                object,
                Throwables.getStackTraceAsString(cause)
        ));
        executor.setEnableEventResend(true);
        final AtomicInteger invokeCount = new AtomicInteger();
        ResendEventObject eventObject = new ResendEventObject(invokeCount);
        executor.executor(new SimpleEventHandler("ResendEventTest"), eventObject);
        executor.awaitTermination(100L, TimeUnit.MILLISECONDS);
        Assert.assertEquals(2, invokeCount.get());
    }

    @Test()
    public void disableResendTest() throws IllegalAccessException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        EventExecutor executor = new EventExecutor(threadPoolExecutor);
        executor.setEventUncaughtExceptionHandler((thread, handler, method, object, cause) ->
                log.error("事件执行失败.\n" +
                         "Thread: {}\nEventHandler: {}\n HandlerMethod: {}\nEventObject: {}\nThrowableStackTrace:\n{}",
                thread,
                handler,
                method,
                object,
                Throwables.getStackTraceAsString(cause)
        ));
        executor.setEnableEventResend(false);
        final AtomicInteger invokeCount = new AtomicInteger();
        ResendEventObject eventObject = new ResendEventObject(invokeCount);
        executor.executor(new SimpleEventHandler("ResendEventTest"), eventObject);
        executor.awaitTermination(100L, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, invokeCount.get());
    }

    @Test
    public void eventResendWithSyncTest() throws IllegalAccessException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        EventExecutor executor = new EventExecutor(threadPoolExecutor);
        executor.setEventUncaughtExceptionHandler((thread, handler, method, object, cause) ->
                log.error("事件执行失败.\n" +
                                "Thread: {}\nEventHandler: {}\n HandlerMethod: {}\nEventObject: {}\nThrowableStackTrace:\n{}",
                        thread,
                        handler,
                        method,
                        object,
                        Throwables.getStackTraceAsString(cause)
                ));
        executor.setEnableEventResend(true);
        final AtomicInteger invokeCount = new AtomicInteger();
        ResendEventObject eventObject = new ResendEventObject(invokeCount);
        executor.addHandler(new SimpleEventHandler("ResendEventTest"));
        executor.executorSync(eventObject);
        threadPoolExecutor.shutdown();
        executor.awaitTermination(100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(2, invokeCount.get());
    }

    @Test()
    public void disableResendWithSyncTest() throws IllegalAccessException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        EventExecutor executor = new EventExecutor(threadPoolExecutor);
        executor.setEventUncaughtExceptionHandler((thread, handler, method, object, cause) ->
                log.error("事件执行失败.\n" +
                                "Thread: {}\nEventHandler: {}\n HandlerMethod: {}\nEventObject: {}\nThrowableStackTrace:\n{}",
                        thread,
                        handler,
                        method,
                        object,
                        Throwables.getStackTraceAsString(cause)
                ));
        executor.setEnableEventResend(false);
        final AtomicInteger invokeCount = new AtomicInteger();
        ResendEventObject eventObject = new ResendEventObject(invokeCount);
        executor.addHandler(new SimpleEventHandler("ResendEventTest"));
        executor.executorSync(eventObject);
        threadPoolExecutor.shutdown();
        executor.awaitTermination(100, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, invokeCount.get());
    }

    @Test
    public void emptyHandlerTest() throws InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                (int) Math.ceil(Runtime.getRuntime().availableProcessors() / 2F),
                30L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(10)
        );
        EventExecutor executor = new EventExecutor(threadPoolExecutor);
        executor.executor(new SimpleEventObject(0, "test", null));
        executor.executorSync(new SimpleEventObject(0, "test", null));
        executor.shutdown(false);
    }

}
