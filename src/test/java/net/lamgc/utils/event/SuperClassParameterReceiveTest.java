package net.lamgc.utils.event;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SuperClassParameterReceiveTest {

    @Test
    public void superClassParameterReceiveTest() throws IllegalAccessException, InterruptedException {
        EventExecutor executor = new EventExecutor(new ThreadPoolExecutor(0, 1, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>()));
        AtomicInteger count = new AtomicInteger();
        executor.addHandler(new EventHandlerA());
        executor.executor(new EventHandlerA(), new EventObjectB(count));
        executor.executor(new EventObjectB(count));
        executor.executorSync(new EventObjectB(count));
        executor.awaitTermination(100L, TimeUnit.MILLISECONDS);
        Assert.assertEquals(count.get(), 3);
    }

    /**
     * Super Class
     */
    public static class EventObjectA implements EventObject {

        private final AtomicInteger countObject;

        EventObjectA(AtomicInteger count) {
            countObject = count;
        }

        public void increment() {
            countObject.getAndIncrement();
        }

    }

    /**
     * Child Class
     */
    public static class EventObjectB extends EventObjectA {

        EventObjectB(AtomicInteger count) {
            super(count);
        }

    }

    public static class EventHandlerA implements EventHandler {

        public void handler(EventObjectA event) {
            System.out.println(event.toString());
            event.increment();
        }

    }

}
