package net.lamgc.utils.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 事件调用异常.
 * <p>用于内部包装{@link InvocationTargetException}异常并传递给{@link EventUncaughtExceptionHandler}处理.</p>
 * @see InvocationTargetException
 * @see EventUncaughtExceptionHandler
 */
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
