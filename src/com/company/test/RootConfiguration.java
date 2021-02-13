package com.company.test;

import com.company.annotation.*;

@Configuration
@ComponentScan(basePackage = "com.company")
public class RootConfiguration {

//    @Component()
//    public Cat cat2(
//            @Value("Cat2")
//                    String name){
//        return new Cat(name);
//    }

//    @Component(initMethod = {"init2", "init"}, destroyMethod = "destroy")
//    public Dog dog1(@Value("Dog1") String dog){
//        return new Dog(dog);
//    }
//////
//    @Component(initMethod = { "init3"})
//    public Dog dog2(@Value("Dog2") String dog){
//        return new Dog(dog);
//    }
//
    @Component(initMethod = "init", destroyMethod = "destroy", scope = Scope.PROTOTYPE)
    public User user2(
//            @Qualifier("cat2")
                    Cat cat,
//                    @Qualifier("dog1")
                            Dog dog1, @Value("User2") String name){
        return new User(cat, dog1, name);
    }

//
//    @Component
////    @Lazy
//    public String name(){
//        return "User2";
//    }
}
