package net.lamgc.utils.event;

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
                Runtime.getRuntime().availableProcessors() / 2,
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
        SimpleEventHandler handler = new SimpleEventHandler("handler1");
        SimpleEventHandler handler2 = new SimpleEventHandler("handler2");
        executor.addHandler(handler);
        executor.addHandler(handler2);
        executor.executor(new SimpleEventObject(1, "HelloWorld", invokeCount));
        Thread.sleep(500L);
        Assert.assertEquals(invokeCount.get(), 2);
        invokeCount.set(0);
        log.info("SingleHandlerTest-----");
        log.info("Execute Count: {}", executor.executor(handler2, new SimpleEventObject(1, "HelloWorld", invokeCount)));
        Thread.sleep(500L);
        Assert.assertEquals(invokeCount.get(), 1);
        invokeCount.set(0);
        executor.removeHandler(handler);
        log.info("deleted Handler");
        executor.executor(new SimpleEventObject(2, "HelloWorld123", invokeCount));
        Thread.sleep(500L);
        Assert.assertEquals(invokeCount.get(), 1);
    }

    @Test
    public void syncExecuteTest() throws IllegalAccessException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                Runtime.getRuntime().availableProcessors() / 2,
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
    }


    @Test
    public void caughtExceptionTest() throws IllegalAccessException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                1,
                Runtime.getRuntime().availableProcessors() / 2,
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

}
