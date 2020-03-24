package net.lamgc.utils.event;

import java.lang.reflect.Method;

class EventInvokeException extends RuntimeException {

    private EventHandler handler;

    private Method handlerMethod;

    private EventObject eventObject;

    public EventInvokeException(EventHandler handler, Method handlerMethod, EventObject event, Throwable cause){
        super(cause);
        setHandler(handler);
        setHandlerMethod(handlerMethod);
        setEventObject(event);
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

    public Method getHandlerMethod() {
        return handlerMethod;
    }

    public void setHandlerMethod(Method handlerMethod) {
        this.handlerMethod = handlerMethod;
    }
}
