package net.lamgc.utils.event;

public class SimpleEventHandler implements EventHandler {

    public void simpleEventHandler1(SimpleEventObject event){
        System.out.println(this.getClass().getSimpleName() + " - [" + event.errorCode + "] " + event.msg);
    }

}
