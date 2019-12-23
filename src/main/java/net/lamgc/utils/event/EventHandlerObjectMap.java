package net.lamgc.utils.event;

/**
 * EventHandler对象存储Map.
 * 用于对EventHandler对象进行存储而无需重新实例化.
 */
public interface EventHandlerObjectMap {

    /**
     * 添加新的EventHandler对象
     * @param eventHandler EventHandler对象
     */
    void addHandlerObject(EventHandler eventHandler);

    /**
     * 通过Class获取EventHandler对象
     * @param eventHandlerClass EventHandler的Class对象
     * @return 如果找到则返回EventHandler对象, 无则返回null
     */
    EventHandler getHandlerObject(Class<?> eventHandlerClass);
}
