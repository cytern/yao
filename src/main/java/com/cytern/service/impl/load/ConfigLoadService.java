package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.load.base.AssetsUnzipLoadService;

import java.util.HashMap;

public class ConfigLoadService {

    private  final HashMap<String,Object> config;

    private final String defaultRobotName;

    private final String masterQqId;

    private final JSONObject dataService;

    private final JSONObject tencent;

    private final String defaultActiveRobot;

    private static volatile ConfigLoadService configLoadService;

    /**
     * 装载配置文件
     */
    private ConfigLoadService() {
        HashMap<String, JSONObject> configLoader = AssetsUnzipLoadService.getInstance().getMods().get("configLoader");
        JSONObject coreConfig = configLoader.get("Core");
        config = new HashMap<>(coreConfig.getInnerMap());
        defaultRobotName = coreConfig.getJSONObject("main").getString("defaultRobotName");
        masterQqId = coreConfig.getJSONObject("main").getString("masterQqId");
        dataService = coreConfig.getJSONObject("main").getJSONObject("dataService");
        tencent = coreConfig.getJSONObject("main").getJSONObject("tencent");
        defaultActiveRobot = coreConfig.getJSONObject("main").getString("defaultActiveRobot");
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


    public String getDefaultRobotName() {
        return defaultRobotName;
    }

    public String getMasterQqId() {
        return masterQqId;
    }

    public JSONObject getDataService() {
        return dataService;
    }

    public JSONObject getTencent() {
        return tencent;
    }


    public String getDefaultActiveRobot() {
        return defaultActiveRobot;
    }
}
