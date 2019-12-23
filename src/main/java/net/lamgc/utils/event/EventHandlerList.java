package net.lamgc.utils.event;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * 事件处理方法列表.
 * 用于根据{@link EventObject}存储事件处理方法.
 */
public interface EventHandlerList {

    /**
     * 获取以指定EventObject为参数的Method
     * @param eventObject 指定的EventObject
     * @return 返回Method set, 如果未找到将返回空Set对象
     */
    Set<Method> getEventHandlerMethod(Class<? extends EventObject> eventObject);

    /**
     * 添加EventHandler.
     * EventHandler内所有形参为(EventObject)的方法收集为事件方法.
     * @param eventHandler EventHandler对象
     * @throws IllegalAccessException 当Method不为Public时抛出
     */
    void addEventHandler(Class<? extends EventHandler> eventHandler) throws IllegalAccessException;

    /**
     * 添加单个EventHandlerMethod
     * @param eventObject 方法所属对象
     * @param eventHandlerMethod 事件处理方法
     */
    void addEventHandlerMethod(Class<? extends EventObject> eventObject, Method eventHandlerMethod);

    /**
     * 通过{@link EventHandler}对象删除EventHandler
     * @param handler 要删除的Handler对象
     */
    default void removeEventHandler(EventHandler handler){
        removeEventHandler(handler.getClass());
    }

    /**
     * 通过{@link EventHandler}Class删除EventHandler
     * @param handler 要删除的Handler Class
     */
    void removeEventHandler(Class<? extends EventHandler> handler);
}
