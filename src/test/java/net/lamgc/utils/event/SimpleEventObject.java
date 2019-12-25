package net.lamgc.utils.event;

import java.util.concurrent.atomic.AtomicInteger;

public class SimpleEventObject implements EventObject {

    public int errorCode;

    public String msg;

    public AtomicInteger invokeCount;

    public SimpleEventObject(int errorCode, String msg, AtomicInteger invokeCount){
        this.errorCode = errorCode;
        this.msg = msg;
        this.invokeCount = invokeCount;
    }
}
