package net.lamgc.utils.event;

public class SimpleEventObject implements EventObject {

    public int errorCode;

    public String msg;

    public SimpleEventObject(int errorCode, String msg){
        this.errorCode = errorCode;
        this.msg = msg;
    }
}
