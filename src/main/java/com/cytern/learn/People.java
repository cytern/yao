package com.cytern.learn;

public class People implements Animal{
    private Integer height;
    @Override
        public String move() {
            System.out.println("walk");
            return "walk";
        }

            public String speak() {
                System.out.println("speak");
                return "speak";
            }

    public People() {
      height = 0;
    }

    public People(Integer peopleHeight) {
        this.height = peopleHeight;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }
}
