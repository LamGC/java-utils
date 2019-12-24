package net.lamgc.utils.event;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EventExecutorTest {

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
        EventExecutor executor = new EventExecutor(threadPoolExecutor);
        SimpleEventHandler handler = new SimpleEventHandler("handler1");
        SimpleEventHandler handler2 = new SimpleEventHandler("handler2");
        executor.addHandler(handler);
        executor.addHandler(handler2);
        executor.executor(new SimpleEventObject(1, "HelloWorld"));
        Thread.sleep(500L);
        System.out.println("OnlyHandlerTest-----");
        System.out.println("Execute Count: " + executor.executor(handler2, new SimpleEventObject(1, "HelloWorld")));
        Thread.sleep(500L);
        executor.removeHandler(handler);
        System.out.println("deleted Handler");
        executor.executor(new SimpleEventObject(2, "HelloWorld123"));
    }



}
