package net.lamgc.utils.event;

public class SimpleEventHandler4 implements EventHandler {

    public void simpleEventHandler1(SimpleEventObject event){
        System.out.println(this.getClass().getSimpleName() + " - 1[" + event.errorCode + "] " + event.msg);
    }

    public void simpleEventHandler2(SimpleEventObject event){
        System.out.println(this.getClass().getSimpleName() + " - 2[" + event.errorCode + "] " + event.msg);
    }

    public void simpleEventHandler3(SimpleEventObject event){
        System.out.println(this.getClass().getSimpleName() + " - 3[" + event.errorCode + "] " + event.msg);
    }

    public void simpleEventHandler4(SimpleEventObject event){
        System.out.println(this.getClass().getSimpleName() + " - 4[" + event.errorCode + "] " + event.msg);
    }

    public void simpleEventHandler5(SimpleEventObject event){
        System.out.println(this.getClass().getSimpleName() + " - 5[" + event.errorCode + "] " + event.msg);
    }

}
