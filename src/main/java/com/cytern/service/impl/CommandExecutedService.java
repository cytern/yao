package com.cytern.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.load.base.RobotCommandLoadService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 指令执行服务
 */
public class CommandExecutedService {


    /**
     * 基础处理指令 方法
     */
    public static void handleCommand(JSONObject requestParams) {
        JSONObject command = requestParams.getJSONObject("command");
        String activeService = command.getString("activeService");
        JSONObject service = RobotCommandLoadService.getInstance().getCommandsMap().get(activeService);
        Method method = (Method) service.get("method");
        JSONObject invoke;
        try {
             invoke = (JSONObject) method.invoke(null, requestParams);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }


    }



}
