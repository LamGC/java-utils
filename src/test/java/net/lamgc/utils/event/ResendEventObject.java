package net.lamgc.utils.event;

import java.util.concurrent.atomic.AtomicInteger;

public class ResendEventObject implements EventObject {

    private final AtomicInteger invokeCount;

    public ResendEventObject(AtomicInteger count) {
        this.invokeCount = count;
    }

    public void addInvokeCount() {
        invokeCount.incrementAndGet();
    }

    public boolean isFirstInvoke() {
        return invokeCount.get() == 1;
    }
}
