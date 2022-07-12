package com.cytern.service.impl;

import cn.hutool.core.util.RandomUtil;
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
        String activeService = requestParams.getString("activeService");
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
    public static JSONObject repeatFilterReturn (JSONArray returnWordRules,JSONObject baseCommand) {
        //如果就剩一个直接返回
        if (returnWordRules.size() == 1) {
            baseCommand.put("finalReturn",returnWordRules.getJSONObject(0));
          return baseCommand;
      }
       Integer totalPresent = 0;
        for (int i = 0; i < returnWordRules.size(); i++) {
            JSONObject singleObj = (JSONObject) returnWordRules.get(i);
            JSONArray repeatFilter = singleObj.getJSONArray("repeatFilter");
            //如果为空直接返回该内容
            if (repeatFilter == null) {
                baseCommand.put("finalReturn",singleObj);
                return baseCommand;
            }
            Integer singlePresent = 0;
            for (int j = 0; j < repeatFilter.size(); j++) {
                String rawString = repeatFilter.getString(i);
                String substring = rawString.substring(rawString.indexOf("("), rawString.indexOf(")"));
                String[] params = substring.split(",");
                Integer present = FilterLoadService.getInstance().handlerSelectExecuted(baseCommand, handlerRawStringToType(params, rawString.substring(0, rawString.indexOf("("))), params);
                singlePresent = present + singlePresent;
            }
            singleObj.put("chooseRate",singlePresent);
            totalPresent = totalPresent + singlePresent;
        }
        int i = RandomUtil.randomInt(totalPresent);
        totalPresent = 0;
        for (int p = 0; p < returnWordRules.size(); p++) {
            JSONObject o =(JSONObject) returnWordRules.get(p);
            Integer chooseRate = o.getInteger("chooseRate");
            if (chooseRate > i) {
                baseCommand.put("finalReturn",returnWordRules.getJSONObject(p-1));
                return baseCommand;
            }
            totalPresent = totalPresent + chooseRate;
        }
        baseCommand.put("finalReturn",returnWordRules.getJSONObject(returnWordRules.size()-1));
        return baseCommand;
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
