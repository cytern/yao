package com.cytern.learn;


public class HomeWork extends paper {

    private String name;
    /**
     * homeWork类已经给出 它继承自paper 类 拥有 write 方法
     * 但是 homeWork 有自己的独有的属性 name 持有人 独有的方法 copy 抄写 实现homework类
     * 新建book类 book 继承 paper  独有方法read
     * test 已经给出 new book homeWork 把他们塞进 paper 集合中 并循环调用 write
     * TODO 提高题 尝试在循环中调用子类独有方法
     */
    public String name() {
        System.out.println("name");
        return "name";
    }
    public String copy() {
        System.out.println("copy");
        return "copy";
    }
}
