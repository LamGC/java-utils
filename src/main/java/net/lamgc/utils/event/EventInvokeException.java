package net.lamgc.utils.event;

import java.lang.reflect.Method;

public class EventInvokeException extends RuntimeException {

    private EventHandler handler;

    private Method handlerMethod;

    private EventObject eventObject;

    private Throwable cause;

    public EventInvokeException(EventHandler handler, Method handlerMethod, EventObject event, Throwable cause){
        super();
        this.handler = handler;
        this.handlerMethod = handlerMethod;
        this.eventObject = event;
        this.cause = cause;
    }

    public EventHandler getHandler() {
        return handler;
    }

    public void setHandler(EventHandler handler) {
        this.handler = handler;
    }

    public EventObject getEventObject() {
        return eventObject;
    }

    public void setEventObject(EventObject eventObject) {
        this.eventObject = eventObject;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    public void setCause(Throwable cause) {
        this.cause = cause;
    }

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public void setHandlerMethod(Method handlerMethod) {
        this.handlerMethod = handlerMethod;
    }
}
