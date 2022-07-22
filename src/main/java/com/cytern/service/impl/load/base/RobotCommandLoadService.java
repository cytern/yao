package com.cytern.service.impl.load.base;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotCommand;
import com.cytern.service.impl.LoggerService;
import com.cytern.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * 机器人本地代码指令方法加载模块 负责扫描包下的 command 方法 并加载到机器人方法容器中
 */
public class RobotCommandLoadService {
    private static volatile RobotCommandLoadService robotCommandLoadService;
    private  final HashMap<String, JSONObject> commandsMap;
    private RobotCommandLoadService()  {
        HashMap<String, JSONObject> waitCommands = new HashMap<>();
        try {

            Set<Class<?>> classByPackage   = ClassUtils.getClasses("com.cytern.command");
            LoggerService.info("打印获取到包下的类为:" + JSONObject.toJSONString(classByPackage));
            try {
                for (Class singleClass : classByPackage) {
                    //类里是否有加了注解的方法 加了 就并入到里面
                    Method[] methods = singleClass.getMethods();
                    for (Method method : methods) {
                        RobotCommand annotation = method.getAnnotation(RobotCommand.class);
                        if (annotation != null) {
                            String activeService = annotation.activeService();
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("class",singleClass);
                            jsonObject.put("method",method);
                            waitCommands.put(activeService,jsonObject);
                        }
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

    public HashMap<String, JSONObject> getCommandsMap() {
        return commandsMap;
    }

}
