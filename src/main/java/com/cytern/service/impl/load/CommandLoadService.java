package com.cytern.service.impl.load;

/**
 * 指令服务  加载所有的本地指令
 */
public class CommandLoadService {

    private static volatile CommandLoadService commandLoadService;

    private CommandLoadService() {

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



}
