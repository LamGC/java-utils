package net.lamgc.utils.event;

import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class EventExecutorTest {

    private static void handler(SimpleEventObject event) {
    }

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
        executor.addHandler(new SimpleEventHandler());
        executor.addHandler(new SimpleEventHandler2());
        executor.addHandler(new SimpleEventHandler3());
        executor.addHandler(new SimpleEventHandler4());
        executor.addHandler(new SimpleEventHandler());
        executor.executor(new SimpleEventObject(1, "HelloWorld"));
        executor.awaitEventExecutor(10L, TimeUnit.SECONDS);
    }



}
