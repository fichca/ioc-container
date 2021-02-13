package com.company.test;

//xml
//java base
//annotation base

import com.company.annotation.*;

//constructor
//setter
//field
@Component()

public class Dog {

    private String name;

    public Dog(@Value("Dog") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PostConstruct
    private void init(){
        System.out.println("InitDog");
    }

    private void init1(){
        System.out.println("Init1Dog");
    }
    private void init2(){
        System.out.println("Init2Dog");
    }
    private void init3(){
        System.out.println("Init3Dog");
    }
    @PreDestroy
    private void destroy(){
        System.out.println("DestroyDog");
    }



    @Override
    public String toString() {
        return "Dog{" +
                "name='" + name + '\'' +
                '}';
    }
}
