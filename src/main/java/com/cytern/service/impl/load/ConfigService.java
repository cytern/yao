package com.cytern.service.impl.load;

public class ConfigService {

    private static volatile ConfigService configService;

    /**
     * 装载配置文件
     */
    private ConfigService() {


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
