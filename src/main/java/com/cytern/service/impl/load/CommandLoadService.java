package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.load.base.AssetsUnzipLoadService;

import java.util.HashMap;

/**
 * 指令服务  加载所有的本地指令
 */
public class CommandLoadService extends AssetsUnzipLoadService {

    private static volatile CommandLoadService commandLoadService;

    private CommandLoadService() {
        super();
        //获取到commandLoad下的全部指令
        HashMap<String, JSONObject> commandLoader = mods.get("commandLoader");



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
        CommandLoadService commandLoadService = new CommandLoadService();
        System.out.println();
    }



}
