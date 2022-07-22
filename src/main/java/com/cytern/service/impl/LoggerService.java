package com.cytern.service.impl;

import com.cytern.Plugin;

public class LoggerService {

    public static void info(String s) {
        Plugin.INSTANCE.getLogger().info(s);
    }

    public static void error(String s) {
        Plugin.INSTANCE.getLogger().warning(s);
    }
}
