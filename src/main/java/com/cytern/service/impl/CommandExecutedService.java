package com.cytern.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.service.impl.load.FilterLoadService;
import com.cytern.service.impl.load.base.RobotCommandLoadService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Collectors;

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

    /**
     * 前筛选器
     */
    public static JSONArray preFilterReturn(JSONObject command) {
        JSONArray returnWordRules = command.getJSONArray("returnWordRules");
        if (returnWordRules.size()<1) {
            throw new RobotException("无效的机器编码 无法获取到任何一个可达到的回复");
        }
        if (returnWordRules.size() == 1) {
            return returnWordRules;
        }
        JSONArray newArray = new JSONArray();
        JSONArray preFilter1 = (JSONArray) returnWordRules.stream().filter(t -> {
            JSONObject singleReturn = (JSONObject) t;
            JSONArray preFilter = singleReturn.getJSONArray("preFilter");
            for (int i = 0; i < preFilter.size(); i++) {
                String rawString = preFilter.getString(i);
                String substring = rawString.substring(rawString.indexOf("("), rawString.indexOf(")"));
                String[] params = substring.split(",");
                boolean b = FilterLoadService.getInstance().handlerFilterExecuted(command, handlerRawStringToType(params, rawString.substring(0, rawString.indexOf("("))), params);
                if (!b) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
        return preFilter1;
    }

    public static void main(String[] args) {

    }

    /**
     * 处理原生语句
     */
    public static String handlerRawStringToType (String[] params,String base) {
        StringBuilder finalString = new StringBuilder(base);
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            try {
                Integer.parseInt(param);
                if (i == 0) {
                    finalString = finalString.append("Integer");
                }else {
                    finalString = finalString.append(",Integer");
                }
            } catch (NumberFormatException e) {
                if (i == 0) {
                    finalString = finalString.append("String");
                }else {
                    finalString = finalString.append(",String");
                }
            }
        }
        return finalString.toString();
    }

    /**
     * 重复筛选器
     */
    public static JSONObject repeatFilterReturn (JSONArray returnWordRules) {
     return null;
    }

    /**
     * 前增强器
     */
    public static JSONObject preReturn (JSONObject command,JSONObject singleReturn) {
      return null;
    }

    /**
     * 处理返回消息
     */
    public static String handleReturnMsg(JSONObject command,JSONObject singleReturn) {
    return null;
    }

    /**
     * 后增强器
     */
    public static void afterReturn (JSONObject singleReturn) {

    }


}
