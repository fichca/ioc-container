package com.company.test;

import com.company.core.Container;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


public class Main {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
//        Class<User> userClass = User.class;
//        Constructor<?>[] declaredConstructors = userClass.getDeclaredConstructors();
//        Class<Cat> catClass = Cat.class;
//        Parameter[] parameters = declaredConstructors[0].getParameters();
//        for (Parameter parameter : parameters) {
//            System.out.println(parameter.getType().isAssignableFrom(catClass));
//        }
        Container container = new Container(RootConfiguration.class);
        System.out.println(container.getAll());
        User user2 = (User) container.getComponent("user2");
        System.out.println(user2);
        container.close();

//        User user = (User) container.getComponent("User");
//        System.out.println(user);
//        System.out.println(container.getAll());
    }
}
