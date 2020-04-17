package net.lamgc.utils.event;

public class SimpleEventHandler implements EventHandler {

    private String flag;

    public SimpleEventHandler(String flag){
        this.flag = flag;
    }

    public void simpleEventHandler(SimpleEventObject event){
        System.out.println(this.toString() + "." + flag + " - [" + event.errorCode + "] " + event.msg);
        event.invokeCount.incrementAndGet();
    }

    public void resendTestEvent(ResendEventObject event) {
        event.addInvokeCount();
        if(event.isFirstInvoke()) {
            EventExecutor.resendCurrentEvent();
        }
    }

    public void throwExceptionEvent(ExceptionThrowEvent event){
        throw new RuntimeException(event.getException());
    }

    protected void protectedEvent(EventObject event) {}

    public void multiParameterMethod(EventObject event, Object obj) {}

    public void nonEventObjectMethod(Object obj) {}
}
