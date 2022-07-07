package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * 加载机器人组件 会加载机器人的会话指令
 */
public final class RobotLoadService {
    private static volatile RobotLoadService robotLoadService;
    /**
     * 机器人集合
     */
    private final HashMap<String,String> robotMap;
    /**
     * 机器人指令集合
     */
    private final HashMap<String, JSONObject> robotCommand;
    private RobotLoadService() {

        robotMap = new HashMap<>();
        robotCommand = new HashMap<>();
    }

    public static RobotLoadService getInstance() {
        if (robotLoadService == null) {
            synchronized (RobotLoadService.class) {
                if (robotLoadService == null) {
                    robotLoadService = new RobotLoadService();
                }
            }
        }
        return robotLoadService;
    }

    public HashMap<String, String> getRobotMap() {
        return robotMap;
    }

    public HashMap<String, JSONObject> getRobotCommand() {
        return robotCommand;
    }
}
