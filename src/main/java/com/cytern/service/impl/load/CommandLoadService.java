package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.load.base.AssetsUnzipLoadService;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 指令服务  加载所有的本地指令
 */
public class CommandLoadService extends AssetsUnzipLoadService {
    private final HashMap<String, JSONObject> localCommands;

    private static volatile CommandLoadService commandLoadService;

    private CommandLoadService() {
        super();
        //获取到commandLoad下的全部指令
        HashMap<String, JSONObject> commandLoader = mods.get("commandLoader");

       localCommands = new HashMap<>();

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
