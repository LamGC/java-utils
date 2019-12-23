package net.lamgc.utils.event;

import java.util.HashMap;

public class HashHandlerObjectMap implements EventHandlerObjectMap {

    private final HashMap<Class<? extends EventHandler>, EventHandler> handlerMap = new HashMap<>();

    @Override
    public void addHandlerObject(EventHandler eventHandler) {
        handlerMap.put(eventHandler.getClass(), eventHandler);
    }

    @Override
    public EventHandler getHandlerObject(Class<?> eventHandlerClass) {
        return handlerMap.get(eventHandlerClass);
    }
}
