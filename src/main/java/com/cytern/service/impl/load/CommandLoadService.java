package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.CommandExecutedService;
import com.cytern.service.impl.load.base.AssetsUnzipLoadService;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 指令服务  加载所有的本地指令
 */
public class CommandLoadService  {
    private final HashMap<String, JSONObject> localCommands;

    private static volatile CommandLoadService commandLoadService;

    private CommandLoadService() {
        //获取到commandLoad下的全部指令
        localCommands = new HashMap<>();
        HashMap<String, JSONObject> commandLoader = AssetsUnzipLoadService.getInstance().getMods().get("commandLoader");
        commandLoader.forEach((key,value) -> {
            JSONObject main = value.getJSONObject("main");
            JSONArray robotCommand = main.getJSONArray("robotCommand");
            if (robotCommand != null) {
                for (int i = 0; i < robotCommand.size(); i++) {
                    JSONObject command = robotCommand.getJSONObject(i);
                    JSONArray activeWordRules = command.getJSONArray("activeWordRules");
                    if (activeWordRules != null) {
                        for (int j = 0; j < activeWordRules.size(); j++) {
                            String rule = activeWordRules.getString(j);
                            localCommands.put(CommandExecutedService.handlerInputKey(rule,command,j),command);
                        }
                    }
                }
            }
        });

    }

    public static CommandLoadService getInstance() {
        if (commandLoadService == null) {
            synchronized (CommandLoadService.class) {
                if (commandLoadService == null) {
                    commandLoadService = new CommandLoadService();
                }
            }
        }
        return commandLoadService;
    }

    public static void main(String[] args) {
        try {
            CommandLoadService commandLoadService = new CommandLoadService();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("121");
        }
        System.out.println();
    }

    public HashMap<String, JSONObject> getLocalCommands() {
        return localCommands;
    }

}
