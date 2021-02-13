package com.company.core;


import com.company.annotation.Scope;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ComponentWrapper {
    private Object o;
    private String [] initMethod;
    private String destroyMethod;
    private Scope scope;
    private Constructor<?> constructor;
    private Method method;
    private String name;

    public ComponentWrapper(Object o, String[] initMethod, String destroyMethod, Scope scope) {
        this.o = o;
        this.initMethod = initMethod;
        this.destroyMethod = destroyMethod;
        this.scope = scope;
    }


    public ComponentWrapper(Constructor<?> constructor) {
        this.constructor = constructor;
    }



    public ComponentWrapper(Method method) {
        this.method = method;
    }

    public ComponentWrapper() {
    }

    public Object getO() {
        return o;
    }

    public void setO(Object o) {
        this.o = o;
    }

    public String[] getInitMethod() {
        return initMethod;
    }

    public void setInitMethod(String[] initMethod) {
        this.initMethod = initMethod;
    }

    public String getDestroyMethod() {
        return destroyMethod;
    }

    public void setDestroyMethod(String destroyMethod) {
        this.destroyMethod = destroyMethod;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ComponentWrapper{" +
                "o=" + o +
                ", initMethod=" + Arrays.toString(initMethod) +
                ", destroyMethod='" + destroyMethod + '\'' +
                ", scope=" + scope +
                ", constructor=" + constructor +
                ", method=" + method +
                '}';
    }
}
