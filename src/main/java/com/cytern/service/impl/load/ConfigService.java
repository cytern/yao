package com.cytern.service.impl.load;

import java.util.HashMap;

public class ConfigService {

    private  final HashMap<String,Object> config;

    private static volatile ConfigService configService;

    /**
     * 装载配置文件
     */
    private ConfigService() {
         config = new HashMap<>();

    }

    public  HashMap<String, Object> getConfig() {
        return config;
    }

    public static ConfigService getInstance() {
        if (configService == null) {
            synchronized (ConfigService.class) {
                if (configService == null) {
                    configService = new ConfigService();
                }
            }
        }
        return configService;
    }
}
