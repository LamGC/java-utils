package net.lamgc.utils.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class BasicEventHandlerList implements EventHandlerList {

    private Hashtable<Class<? extends EventObject>, HashSet<Method>> eventMethodMap = new Hashtable<>();

    @Override
    public Set<Method> getEventHandlerMethod(final Class<? extends EventObject> eventObject) {
        HashSet<Method> methods = new HashSet<>();
        eventMethodMap.keySet().forEach(clazz -> {
            if(clazz.isAssignableFrom(eventObject)) {
                methods.addAll(eventMethodMap.get(clazz));
            }
        });
        return methods;
    }

    @Override
    public void addEventHandler(Class<? extends EventHandler> eventHandler) throws IllegalAccessException {
        int classModifier = eventHandler.getModifiers();
        if(!Modifier.isPublic(classModifier)) {
            throw new IllegalAccessException("class is not public");
        } else if(Modifier.isInterface(classModifier) || Modifier.isAbstract(classModifier)) {
            throw new IllegalStateException("Class is an interface or abstract");
        }

        Method[] methods = eventHandler.getDeclaredMethods();
        for(Method method : methods){
            Class<?>[] parameterTypes = method.getParameterTypes();
            if(!EventExecutor.checkMethod(method)) {
                continue;
            }
            addEventHandlerMethod(parameterTypes[0].asSubclass(EventObject.class), method);
        }
    }

    @Override
    public void addEventHandlerMethod(Class<? extends EventObject> eventObject, Method eventHandlerMethod) {
        if(!eventMethodMap.containsKey(eventObject)){
            eventMethodMap.put(eventObject, new HashSet<>());
        }
        eventMethodMap.get(eventObject).add(eventHandlerMethod);
    }

    @Override
    public void removeEventHandler(Class<? extends EventHandler> handler) {
        Method[] methods = handler.getDeclaredMethods();
        for(Method method : methods){
            int modifiers = method.getModifiers();
            if(!Modifier.isPublic(modifiers)){
                continue;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if(parameterTypes.length != 1){
                continue;
            }
            if(!EventObject.class.isAssignableFrom(parameterTypes[0])){
                continue;
            }
            eventMethodMap.get(parameterTypes[0]).remove(method);
        }
    }
}
