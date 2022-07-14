package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.CommandExecutedService;
import com.cytern.service.impl.load.base.AssetsUnzipLoadService;

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
        //加载机器人
        HashMap<String, JSONObject> robotLoader = AssetsUnzipLoadService.getInstance().getMods().get("robotLoader");
        robotLoader.forEach((key,value) -> {
            String robotName = value.getJSONObject("config").getString("modName");
            robotMap.put(robotName,value.getJSONObject("config").getString("modCode"));
            JSONArray command = value.getJSONObject("main").getJSONArray("robotCommand");
            for (int i = 0; i < command.size(); i++) {
                JSONObject singleCommand = command.getJSONObject(i);
                JSONArray activeWordRules = singleCommand.getJSONArray("activeWordRules");

                if (activeWordRules != null) {
                    for (int j = 0; j < activeWordRules.size(); j++) {
                        String ruleString = activeWordRules.getString(j);
                        robotCommand.put(CommandExecutedService.handlerInputKey(ruleString,singleCommand,j),singleCommand);
                    }
                }
            }
        });
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
