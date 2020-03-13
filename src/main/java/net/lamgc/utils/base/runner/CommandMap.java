package net.lamgc.utils.base.runner;

import java.lang.reflect.Method;
import java.util.HashMap;

class CommandMap extends HashMap<String, Method> {

    private Method defaultMethod = null;

    public CommandMap(int initialCapacity) {
        super(initialCapacity);
    }

    public Method getDefaultMethod() {
        return defaultMethod;
    }

    public void setDefaultMethod(Method defaultMethod) {
        this.defaultMethod = defaultMethod;
    }

    public boolean hasDefaultMethod() {
        return defaultMethod != null;
    }

}
