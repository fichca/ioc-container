package com.company.core;


import com.company.annotation.Component;
import com.company.annotation.Qualifier;
import com.company.annotation.Scope;
import com.company.annotation.Value;

import java.lang.reflect.*;
import java.util.*;

public class Factory {

    private final Class<?> configurationClass;
    private final Map<String, ComponentWrapper> stringObjectMap;
    private final Map<String, ComponentWrapper> lazyObjectMap;
    private final Set<Class<?>> typesAnnotatedWith;
    private final Object objectConfigurationClass;

    public Factory(Class<?> configurationClass, Map<String, ComponentWrapper> stringObjectMap, Map<String, ComponentWrapper> lazyObjectMap, Set<Class<?>> typesAnnotatedWith, Object objectConfigurationClass) {
        this.configurationClass = configurationClass;
        this.stringObjectMap = stringObjectMap;
        this.lazyObjectMap = lazyObjectMap;
        this.typesAnnotatedWith = typesAnnotatedWith;
        this.objectConfigurationClass = objectConfigurationClass;
    }




    public void addToMapFromPackageConfiguration() {
        if (typesAnnotatedWith.size() == 0) return;
        List<Constructor<?>> listConstructors = Util.getListConstructors(typesAnnotatedWith, lazyObjectMap);
        for (Constructor<?> constructor : listConstructors) {
            addToMapConstructorWithConfiguration(constructor);
        }
    }

    public void addToMapConstructorWithConfiguration(Constructor<?> constructor) {
        ComponentWrapper componentWrapper = new ComponentWrapper();
//        Object[] parameters = Util.getParameters(constructor.getParameters(), stringObjectMap);
//        ComponentWrapper componentWrapper = Util.getComponentWrapper(constructor, parameters);
        componentWrapper.setConstructor(constructor);
        String[] split = constructor.getName().split("\\.");
        String key = split[split.length - 1].toLowerCase();
        componentWrapper.setName(key);
        componentWrapper.setInitMethod(null);
        componentWrapper.setDestroyMethod("PACKAGE");
        componentWrapper.setScope(Scope.SINGLETON);
        stringObjectMap.put(key, componentWrapper);
    }

    public void addToMapFromClassConfiguration() {

        Method[] methodsOfConfigClass = configurationClass.getDeclaredMethods();
        for (Method method : methodsOfConfigClass) {
            ComponentWrapper componentWrapper = new ComponentWrapper(method);
            if (method.isAnnotationPresent(Constant.initAnnotation)) {
                String keyMap = method.getName();
                componentWrapper.setName(keyMap);
                if (method.isAnnotationPresent(Constant.LAZY)) {
                    lazyObjectMap.put(keyMap, componentWrapper);
                } else {
                    stringObjectMap.put(keyMap, componentWrapper);
                    setInitAnnotationInMap(method.getAnnotation(Constant.initAnnotation), keyMap, stringObjectMap);

                }
            }
        }
    }


    public void initAllFromMap() {
        for (Map.Entry<String, ComponentWrapper> objectMap : stringObjectMap.entrySet()) {
            ComponentWrapper componentWrapper = objectMap.getValue();
            if (componentWrapper.getO() != null) continue;
            try {
                initWrapper(componentWrapper, objectConfigurationClass, stringObjectMap);
            } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public static void initWrapper(ComponentWrapper componentWrapper, Object objectConfigurationClass, Map<String, ComponentWrapper> stringObjectMap) throws InvocationTargetException, IllegalAccessException, InstantiationException {

        if (componentWrapper.getMethod() != null) {
            Method method = componentWrapper.getMethod();
            Parameter[] parametersMethod = method.getParameters();
            Object[] parameters = getParameters(parametersMethod, stringObjectMap, objectConfigurationClass);
            Object invoke = method.invoke(objectConfigurationClass, parameters);
            componentWrapper.setO(invoke);
        } else {
            Constructor<?> constructor = componentWrapper.getConstructor();
            Object[] parameters = getParameters(constructor.getParameters(), stringObjectMap, objectConfigurationClass);
            Object o = constructor.newInstance(parameters);
            componentWrapper.setO(o);
        }
    }

    protected static Object[] getParameters(Parameter[] parameters, Map<String, ComponentWrapper> stringObjectMap, Object objectConfigurationClass) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        List<Object> objects = new ArrayList<>();
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(Constant.valueAnnotation)) {
                Value annotation = parameter.getAnnotation(Constant.valueAnnotation);
                objects.add(annotation.value());
            } else if (parameter.isAnnotationPresent(Constant.qualifierAnnotation)) {
                Qualifier annotation = parameter.getAnnotation(Constant.qualifierAnnotation);
                ComponentWrapper componentWrapper = stringObjectMap.get(annotation.value().toLowerCase());
                if (componentWrapper.getO() == null) {
                    initWrapper(componentWrapper, objectConfigurationClass, stringObjectMap);
                }
                objects.add(componentWrapper.getO());
            } else {
                String name = parameter.getType().getSimpleName();
                if (stringObjectMap.get(name.toLowerCase()) != null) {
                    ComponentWrapper componentWrapper = stringObjectMap.get(name.toLowerCase());
                    if (componentWrapper.getO() == null) {
                        initWrapper(componentWrapper, objectConfigurationClass, stringObjectMap);
                    }
                    objects.add(componentWrapper.getO());

                } else {

                    Class<?> type = parameter.getType();
                    for (Map.Entry<String, ComponentWrapper> stringComponentWrapperEntry : stringObjectMap.entrySet()) {
                        ComponentWrapper componentWrapper = stringComponentWrapperEntry.getValue();

                        if (componentWrapper.getO() != null) {
                            Object o = componentWrapper.getO();
                            if (type.isAssignableFrom(o.getClass())) {
                                objects.add(o);
                                break;
                            }
                        }

                        if (componentWrapper.getMethod() != null) {
                            Method method = componentWrapper.getMethod();
                            Class<? extends Method> aClass = method.getClass();
                            if (type.isAssignableFrom(aClass)) {

                            }
                        } else {
                            Constructor<?> constructor = componentWrapper.getConstructor();

                            if (type.isAssignableFrom(constructor.getDeclaringClass())) {
                                initWrapper(componentWrapper, objectConfigurationClass, stringObjectMap);
                                objects.add(componentWrapper.getO());
                                break;
                            }
                        }
                    }
                }
            }
        }
        return objects.toArray();
    }



    private void setInitAnnotationInMap(Component annotation, String name, Map<String, ComponentWrapper> stringObjectMap) {
        String destroyMethod = annotation.destroyMethod();
        if (destroyMethod.equals("")) {
            destroyMethod = null;
        }
        Scope scope = annotation.scope();
        ComponentWrapper componentWrapper = stringObjectMap.get(name);
        componentWrapper.setInitMethod(annotation.initMethod());
        componentWrapper.setDestroyMethod(destroyMethod);
        componentWrapper.setScope(scope);
    }


    public void initField(Map<String, ComponentWrapper> stringObjectMap) throws IllegalAccessException {
        for (Map.Entry<String, ComponentWrapper> stringComponentWrapperEntry : stringObjectMap.entrySet()) {
            Object o = stringComponentWrapperEntry.getValue().getO();
            Field[] declaredFields = o.getClass().getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(Constant.INITIALIZATION_ANNOTATION)) {
                    declaredField.setAccessible(true);
                    String object;
                    if (declaredField.isAnnotationPresent(Constant.qualifierAnnotation)) {
                        object = declaredField.getAnnotation(Constant.qualifierAnnotation).value();
                    } else {
                        object = declaredField.getName().toLowerCase();
                    }
                    declaredField.set(o, object);
                }
            }
        }
    }
}
