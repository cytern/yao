package com.cytern.util;

import com.alibaba.fastjson.JSONObject;
import com.cytern.CommonMessageProcessor;

public class RunTest {
    public static void main(String[] args) {
        String rawString = "是否机器人主人()";
        String substring = rawString.substring(rawString.indexOf("(")+1, rawString.indexOf(")"));
        String[] params = substring.split(",");
        System.out.println(substring);
    }
}
