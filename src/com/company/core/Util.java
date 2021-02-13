package com.company.core;



import com.company.annotation.Qualifier;
import com.company.annotation.Value;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class Util {




    protected static List<Constructor<?>> getListConstructors(Set<Class<?>> typesAnnotatedWith, Map<String, ComponentWrapper> lazyObjectMap) {
        List<Constructor<?>> list = new ArrayList<>();
        for (Class<?> aClass : typesAnnotatedWith) {
            if (aClass.isAnnotationPresent(Constant.LAZY)) {
                lazyObjectMap.put(aClass.getSimpleName(), new ComponentWrapper(getConstructor(aClass)));
            } else {
                list.add(getConstructor(aClass));
            }
        }
        return list;
    }

    public static Constructor<?> getConstructor(Class<?> aClass) {
        Constructor<?>[] declaredConstructors = aClass.getDeclaredConstructors();
        if (aClass.getDeclaredConstructors().length == 1) {
            return declaredConstructors[0];
        } else {
            for (Constructor<?> declaredConstructor : declaredConstructors) {
                if (declaredConstructor.isAnnotationPresent(Constant.INITIALIZATION_ANNOTATION)) {
                    return declaredConstructor;
                }
            }
            throw new NoSuchElementException();
        }
    }




    protected static void invokeMethodWithoutParam(Object o, String nameMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> aClass = o.getClass();
        Method declaredMethod = aClass.getDeclaredMethod(nameMethod);
        declaredMethod.setAccessible(true);
        declaredMethod.invoke(o);
    }
}
