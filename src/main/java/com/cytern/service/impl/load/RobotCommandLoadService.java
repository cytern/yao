package com.cytern.service.impl.load;

import com.cytern.aspect.RobotCommand;
import com.cytern.command.RobotCommands;
import com.cytern.util.ClassUtils;

import java.util.HashMap;

/**
 * 机器人指令
 */
public class RobotCommandLoadService {
    private static volatile RobotCommandLoadService robotCommandLoadService;
    private  final HashMap<String,RobotCommands> commandsMap;
    private RobotCommandLoadService()  {
        HashMap<String, RobotCommands> waitCommands = new HashMap<>();
        try {
            Class[] classByPackage = ClassUtils.getClassByPackage("com.cytern.command");
            try {
                for (Class singleClass : classByPackage) {
                    //判断class 文件是否有注解 如果有的话 那就要加入到commandList 里
                    RobotCommand annotation = (RobotCommand) singleClass.getAnnotation(RobotCommand.class);
                    if (annotation != null) {
                       //默认构造器即可
                        RobotCommands o =(RobotCommands) singleClass.newInstance();
                        String activeService = annotation.activeService();
                        waitCommands.put(activeService,o);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        commandsMap = waitCommands;
    }
    public static RobotCommandLoadService getInstance() {
        if (robotCommandLoadService == null) {
            synchronized (ModLoadService.class) {
                if (robotCommandLoadService == null) {
                    robotCommandLoadService = new RobotCommandLoadService();
                }
            }
        }
        return robotCommandLoadService;
    }

    public HashMap<String, RobotCommands> getCommandsMap() {
        return commandsMap;
    }

}
