package com.thelatest.testModel;

public class Demo {
    private String name;
    private int age;

    // returns a string
    public String getName() {
        return name;
    }
    // doesn't return a value, uses camelCase for custom name, and custom arguments of specified type
    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Demo(String name, int age) {
        this.name = name;
        this.age = age;

    }
}
