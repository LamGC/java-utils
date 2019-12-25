package net.lamgc.utils.event;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HashHandlerObjectMap implements EventHandlerObjectMap {

    private final HashMap<Class<? extends EventHandler>, HashSet<EventHandler>> handlerMap = new HashMap<>();

    @Override
    public void addHandlerObject(EventHandler eventHandler) {
        if(!handlerMap.containsKey(eventHandler.getClass())){
            handlerMap.put(eventHandler.getClass(), new HashSet<>());
        }
        handlerMap.get(eventHandler.getClass()).add(eventHandler);
    }

    @Override
    public Set<EventHandler> getHandlerObject(Class<?> eventHandlerClass) {
        return handlerMap.get(eventHandlerClass);
    }

    @Override
    public boolean removeHandlerObject(EventHandler handler) {
        HashSet<EventHandler> eventHandlers = handlerMap.get(handler.getClass());
        eventHandlers.remove(handler);
        return !eventHandlers.isEmpty();
    }
}
