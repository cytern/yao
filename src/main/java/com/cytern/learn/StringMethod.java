package com.cytern.learn;

public class StringMethod {
    public static void main(String[] args) {
        String name = "asdasda"; //存字符串 ['a','s','d','a','s','d','a']
        byte[] bytes = name.getBytes();
        for (int i = 0; i < 3; i++) {
            System.out.println(bytes[i]);
        }
        String newName = name.substring(1, 2);//切割数组 里面对比下长度入参是否合法 合法的话 新建一个string  给他  0 5
        newName = "asdasda";
        if (name.equals(newName)) {
           //字符串是否相等
        }
        //TODO 寻找String的 其他方法 并 添加注释
        String newName2 = "asdasda";
        System.out.println(newName2.concat("abc"));//在字符后面添加括号里的字符

        String newName3 = name.toUpperCase();//字符换成大写 toLowerCase则相反
        System.out.print(name.toUpperCase());

        new Object();
        System.out.println();
    }
}
