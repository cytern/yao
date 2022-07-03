package com.cytern.learn;

public class Fish implements Animal{
    @Override
    public String move() {
        System.out.println("swim");
        return "swim";
    }
}
