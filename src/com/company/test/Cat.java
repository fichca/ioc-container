package com.company.test;

import com.company.annotation.*;
import org.reflections.Reflections;

import java.util.Set;

@Component
public class Cat implements CatInterface{


    private String name;

//    public static void main(String[] args) {
//        Reflections reflections = new Reflections("com.company");
//        Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(Component.class);
//    }

    public Cat(@Value("Cat")String name) {
        this.name = name;
    }

    @PostConstruct
    private void init(){
        System.out.println("Init");
    }
    @PreDestroy
    private void destroy(){
        System.out.println("Destroy");
    }

    @Override
    public String toString() {
        return "Cat{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean getHand(Dog dog) {
        return false;
    }
}
