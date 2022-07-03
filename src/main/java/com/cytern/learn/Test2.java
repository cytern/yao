package com.cytern.learn;

import java.util.ArrayList;
import java.util.List;

public class Test2 {

    /**
     *     //animal2.watchDoor();    这个代码是错误的，父类的引用不能直接调用子类的方法
     *          Dog dog = (Dog)animal2;  //将父类引用强制转换成子类，功能变强大了
     *         dog.watchDoor();   // 可以执行子类独有的方法
     * ————————————————
     * 版权声明：本文为CSDN博主「孤枫_J」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     * 原文链接：https://blog.csdn.net/qq_35809245/article/details/52214966
     */
    public static void main(String[] args) {

            book book = new book();
            HomeWork homeWork = new HomeWork();
            List<paper> papers = new ArrayList<>();
            papers.add(book);
            papers.add(homeWork);
            for (int i = 0; i < papers.size(); i++) {
                paper paper = papers.get(i);
                paper.write();
                try {
                    HomeWork homeWork1 = (HomeWork)paper;
                    homeWork1.copy();
                } catch (Exception e) {

                }
            }

    }
}
