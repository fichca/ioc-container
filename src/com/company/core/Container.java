package com.company.core;


import com.company.annotation.Scope;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Container {

    private Class<?> configurationClass;
    private Set<Class<?>> typesAnnotatedWith;
    private final Factory factory;
    private final Object objectConfigurationClass;

    private final Map<String, ComponentWrapper> stringObjectMap = new HashMap<>();
    private final Map<String, ComponentWrapper> lazyObjectMap = new HashMap<>();


    public Container(Class<?> configurationClass) {
        this.configurationClass = configurationClass;
        this.objectConfigurationClass = newInstance(configurationClass);
        initializationPackageAnnotated(configurationClass);
        factory = new Factory(configurationClass, stringObjectMap, lazyObjectMap, typesAnnotatedWith, objectConfigurationClass);
        init();
        initAnnotation();
    }

    private Object newInstance(Class<?> configurationClass) {
        Constructor<?>[] declaredConstructors = configurationClass.getDeclaredConstructors();
        try {
            Object o = declaredConstructors[0].newInstance();
            return o;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        throw new NoSuchElementException();
    }

    private void initializationPackageAnnotated(Class<?> configurationClass) {

        if (!configurationClass.isAnnotationPresent(Constant.configurationAnnotation)) return;
        if (configurationClass.isAnnotationPresent(Constant.packageAnnotation)) {
            String s = configurationClass.getAnnotation(Constant.packageAnnotation).basePackage();
            Reflections reflections = new Reflections(s);
            this.typesAnnotatedWith = reflections.getTypesAnnotatedWith(Constant.initAnnotation);
            return;
        }
        typesAnnotatedWith = new HashSet<>();
    }

    private void init() {
        factory.addToMapFromClassConfiguration();
        factory.addToMapFromPackageConfiguration();
        factory.initAllFromMap();

        try {
            factory.initField(stringObjectMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private void initAnnotation() {

        for (Map.Entry<String, ComponentWrapper> stringComponentWrapperEntry : stringObjectMap.entrySet()) {
            Object o = stringComponentWrapperEntry.getValue().getO();
            if (stringComponentWrapperEntry.getValue().getInitMethod() != null) {
                for (String s : stringComponentWrapperEntry.getValue().getInitMethod()) {
                    try {
                        Util.invokeMethodWithoutParam(o, s);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                for (Method declaredMethod : o.getClass().getDeclaredMethods()) {
                    if (declaredMethod.isAnnotationPresent(Constant.POST_CONSTRUCT_ANNOTATION)) {
                        try {
                            declaredMethod.setAccessible(true);
                            declaredMethod.invoke(o);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public List<Object> getAll() {
        Set<Map.Entry<String, ComponentWrapper>> entries = stringObjectMap.entrySet();
        List<Object> list = new ArrayList<>();
        for (Map.Entry<String, ComponentWrapper> entry : entries) {
            list.add(entry.getValue().getO());
        }
        return list;
    }

    public Object getComponent(String name) {

        if (lazyObjectMap.get(name) != null) {
            ComponentWrapper componentWrapper = lazyObjectMap.get(name);
            try {
                Factory.initWrapper(componentWrapper, objectConfigurationClass, stringObjectMap);
                factory.initField(stringObjectMap);
                lazyObjectMap.remove(name);
            } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        ComponentWrapper componentWrapper = stringObjectMap.get(name.toLowerCase());
        if (componentWrapper.getScope().equals(Scope.PROTOTYPE)) {
            String s = "clone";
            try {
                Object o = componentWrapper.getO();
                Method declaredMethod = o.getClass().getDeclaredMethod(s);
                declaredMethod.setAccessible(true);
                return declaredMethod.invoke(o);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            throw new NoSuchElementException();

        } else {
            return componentWrapper.getO();
        }

    }


    public void close() {
        Set<Map.Entry<String, ComponentWrapper>> entries = new HashSet<>(stringObjectMap.entrySet());

        for (Map.Entry<String, ComponentWrapper> stringComponentWrapperEntry : entries) {
            ComponentWrapper value = stringComponentWrapperEntry.getValue();
            Object o = value.getO();
            if (value.getDestroyMethod() != null) {
                if (value.getDestroyMethod().equals("PACKAGE")) {
                    for (Method declaredMethod : o.getClass().getDeclaredMethods()) {
                        if (declaredMethod.isAnnotationPresent(Constant.PRE_DESTROY_ANNOTATION)) {
                            declaredMethod.setAccessible(true);
                            try {
                                declaredMethod.invoke(o);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } else {
                    try {
                        Util.invokeMethodWithoutParam(o, value.getDestroyMethod());
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            stringObjectMap.remove(stringComponentWrapperEntry.getKey());
        }
        stringObjectMap.clear();
    }
}
