package com.cytern.learn;

import java.util.ArrayList;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        List<Animal> animals = new ArrayList<>();
        Fish fish = new Fish();
        People people = new People();
        Dog dog = new Dog();
        Child child = new Child();
        animals.add(fish);
        animals.add(dog);
        animals.add(people);
        animals.add(child);
        child.speak();
        run(animals);

    }

    private static void run(List<Animal> animals) {
        for (Animal animal : animals) {
            //TODO
            animal.move();
        }
    }
}
