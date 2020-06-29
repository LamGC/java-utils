package net.lamgc.utils.event;

/**
 * 用于存储事件处理方法的类所需要实现的空接口.<br>
 * 标识该类存在事件处理方法.<br>
 * 注意:<br>
 * 1. 不要让HandlerMethod与线程关联, 应该让handlerMethod只使用传入EventObject中的参数来处理, 尽可能的保持事件处理的独立性;
 */
public interface EventHandler {

}
