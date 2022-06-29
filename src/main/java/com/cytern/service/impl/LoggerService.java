package com.cytern.service.impl;

import com.cytern.Plugin;

public class LoggerService {

    public static void info(String s) {
//        Plugin.INSTANCE.getLogger().info(s);
        System.out.println(s);
    }

    public static void error(String s) {
//        Plugin.INSTANCE.getLogger().error(s);
        RuntimeException runtimeException = new RuntimeException(s);
        runtimeException.printStackTrace();
    }
}
