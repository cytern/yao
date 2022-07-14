package com.cytern.service.impl.load;

import java.util.HashMap;

public class ConfigLoadService {

    private  final HashMap<String,Object> config;

    private static volatile ConfigLoadService configLoadService;

    /**
     * 装载配置文件
     */
    private ConfigLoadService() {
         config = new HashMap<>();

         config.put("defaultRobotName","阿斯");

    }

    public  HashMap<String, Object> getConfig() {
        return config;
    }

    public static ConfigLoadService getInstance() {
        if (configLoadService == null) {
            synchronized (ConfigLoadService.class) {
                if (configLoadService == null) {
                    configLoadService = new ConfigLoadService();
                }
            }
        }
        return configLoadService;
    }
}
