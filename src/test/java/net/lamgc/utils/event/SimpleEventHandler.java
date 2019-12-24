package net.lamgc.utils.event;

public class SimpleEventHandler implements EventHandler {

    private String flag;

    public SimpleEventHandler(String flag){
        this.flag = flag;
    }

    public void simpleEventHandler(SimpleEventObject event){
        System.out.println(this.toString() + "." + flag + " - [" + event.errorCode + "] " + event.msg);
    }

    public void throwExceptionEvent(ExceptionThrowEvent event){
        throw new RuntimeException(event.getException());
    }

}
