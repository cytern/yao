package com.cytern.learn;

public class People implements Animal{
    @Override
        public String move() {
            System.out.println("walk");
            return "walk";
        }

            public String speak() {
                System.out.println("speak");
                return "speak";
            }

}
