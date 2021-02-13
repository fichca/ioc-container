package com.company.test;

import com.company.annotation.*;

import java.util.Objects;

//@Component()

public class User implements Cloneable{

//    @Autowired
    private CatInterface cat;

//    @Autowired
//    @Qualifier("dog1")
    private Dog dog;

//    @Value("User")
    private String name;

//    @Autowired
    public User(CatInterface cat, @Qualifier("Dog") Dog dog, @Value("User") String name) {
        this.cat = cat;
        this.dog = dog;
        this.name = name;
    }
//
//    public User(String name) {
//        this.name = name;
//    }
    @PostConstruct
    private void init() {
        System.out.println("InitUser");
    }
    @PreDestroy
    private void destroy() {
        System.out.println("DestroyUser");
    }



    public void setCat(Cat cat) {
        this.cat = cat;
    }

    public Dog getDog() {
        return dog;
    }

    public void setDog(Dog dog) {
        this.dog = dog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(cat, user.cat) && Objects.equals(dog, user.dog) && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cat, dog, name);
    }

    @Override
    public String toString() {
        return "User{" +
                "cat=" + cat +
                ", dog=" + dog +
                ", name='" + name + '\'' +
                '}';
    }
}
