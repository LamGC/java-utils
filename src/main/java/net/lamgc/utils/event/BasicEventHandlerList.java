package net.lamgc.utils.event;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class BasicEventHandlerList implements EventHandlerList {

    private HashMap<Class<? extends EventObject>, HashSet<Method>> eventMethodMap = new HashMap<>();

    @Override
    public Set<Method> getEventHandlerMethod(Class<? extends EventObject> eventObject) {
        return eventMethodMap.get(eventObject);
    }

    @Override
    public void addEventHandler(Class<? extends EventHandler> eventHandler) {
        Method[] methods = eventHandler.getDeclaredMethods();
        for(Method method : methods){
            int modifiers = method.getModifiers();
            if(!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)){
                //System.out.println("方法不是公开的/抽象方法/接口方法: " + modifiers);
                continue;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if(parameterTypes.length != 1){
                //System.out.println("参数过多: " + parameterTypes.length);
                continue;
            }
            if(!EventObject.class.isAssignableFrom(parameterTypes[0])){
                //System.out.println("不是EventObject子类");
                continue;
            }
            addEventHandlerMethod((Class<? extends EventObject>) parameterTypes[0], method);
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
            if(!Modifier.isPublic(modifiers) || Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)){
                //System.out.println("方法不是公开的/抽象方法/接口方法: " + modifiers);
                continue;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if(parameterTypes.length != 1){
                //System.out.println("参数过多: " + parameterTypes.length);
                continue;
            }
            if(!EventObject.class.isAssignableFrom(parameterTypes[0])){
                //System.out.println("不是EventObject子类");
                continue;
            }
            eventMethodMap.get(parameterTypes[0]).remove(method);
        }
    }
}
