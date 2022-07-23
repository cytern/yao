package com.cytern.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.pojo.ErrorCode;
import com.cytern.service.impl.load.AdviceLoadService;
import com.cytern.service.impl.load.FilterLoadService;
import com.cytern.service.impl.load.base.RobotCommandLoadService;
import com.cytern.util.MessageSenderUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;

/**
 * 指令执行服务
 */
public class CommandExecutedService {


    /**
     * 基础处理指令 方法
     */
    public static void handleCommand(JSONObject requestParams) {
        String activeService = requestParams.getString("activeService");
        LoggerService.info("准备要获取服务:" + activeService);
        JSONObject service = RobotCommandLoadService.getInstance().getCommandsMap().get(activeService);
        if (service!= null) {
            Method method = (Method) service.get("method");
            JSONObject invoke;
            try {
                 invoke = (JSONObject) method.invoke(null, requestParams);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
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
        JSONArray returns = new JSONArray();
        if (returnWordRules.size() == 1) {
            return returnWordRules;
        }
        for (int i = 0; i < returnWordRules.size(); i++) {
            JSONArray unPassArray = new JSONArray();
            JSONObject singleReturn = returnWordRules.getJSONObject(i);
            JSONArray preFilter = singleReturn.getJSONArray("preFilter");
            if (preFilter!= null) {
                for (int j = 0; j < preFilter.size(); j++) {
                    String rawString = preFilter.getString(j);
                    if (rawString.equals("")) {
                        continue;
                    }
                    String substring = rawString.substring(rawString.indexOf("(")+1, rawString.indexOf(")"));
                    String[] params = substring.split(",");
                    boolean b = FilterLoadService.getInstance().handlerFilterExecuted(command, handlerRawStringToType(params, rawString.substring(0, rawString.indexOf("("))), params);
                    if (!b) {
                        unPassArray.add(preFilter);
                        break;
                    }
                }
            }else {
                //如果前置筛选器为空 直接返回当前返回
                return new JSONArray(Collections.singletonList(singleReturn));
            }
            if (unPassArray.size() == 0) {
                returns.add(singleReturn);
            }
        }
        if (returns.size()<1) {
            throw new RobotException("前置筛选器下没有正确的返回值");
        }
        return returns;
    }



    /**
     * 处理原生语句
     */
    public static String handlerRawStringToType (String[] params,String base) {
        StringBuilder finalString = new StringBuilder(base);
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            if (params.length == 1 && !params[0].equals("")) {
                finalString.append("(").append("String").append(")");
            }else if (params.length == 1) {
                finalString.append("(").append(")");
            }
            else if (i == 0) {
                finalString.append("(");
                finalString.append("String");
            }else if (i == params.length -1){
                finalString.append(",String)");
            }else {
                finalString.append(",String");
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
            //如果为空 那就要这条
            if (repeatFilter == null|| repeatFilter.size() ==0) {
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
        singleReturn = command.getJSONObject("finalReturn");
        JSONArray preReturn = singleReturn.getJSONArray("preReturn");
        if (preReturn != null) {
            for (int i = 0; i < preReturn.size(); i++) {
                String rawString = preReturn.getString(i);
                String substring = rawString.substring(rawString.indexOf("(")+1, rawString.indexOf(")"));
                String[] params = substring.split(",");
                command = AdviceLoadService.getInstance().handlerAdviceExecuted(command,handlerRawStringToType(params, rawString.substring(0, rawString.indexOf("("))), params,i);
            }
        }
        return command;

    }

    /**
     * 处理返回消息
     */
    public static MessageChain handleReturnMsg(JSONObject command) {
        JSONObject finalReturn = command.getJSONObject("finalReturn");
        String returnMsg = finalReturn.getString("returnMsg");
        MessageChainBuilder chain = new MessageChainBuilder();
        if (!returnMsg.contains("《") && !returnMsg.contains("》")) {
            return chain.append(returnMsg).build();
        }else {
            String[] split = returnMsg.split("\\《|\\》");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (s.contains("爻服务") || s.contains("爻入参")) {
                  if (s.contains(".")) {
                      String[] split1 = s.split("\\.");
                      JSONObject tempObj = new JSONObject(command);
                      for (int j = 0; j < split1.length; j++) {
                          String s1 = split1[j];
                          if (j != split1.length -1) {
                              if (tempObj != null && tempObj.containsKey(s1)){
                                  tempObj = tempObj.getJSONObject(s1);
                              }else {
                                  throw new RobotException(ErrorCode.PARAM_SPLICE_NOT_RIGHT.getMsg(1));
                              }

                          }else {
                              if (tempObj != null && tempObj.containsKey(s1)){
                                  chain.append(tempObj.getString(s1));
                              }else {
                                  throw new RobotException(ErrorCode.PARAM_SPLICE_NOT_RIGHT.getMsg(2));
                              }
                          }
                      }
                  }
                }else if (s.contains("爻图片")) {
                    String miraiCode = MessageSenderUtil.uploadAndReplaceImage(s, (Bot) command.get("currentBot"));
                    chain.append(Image.fromId(miraiCode));
                }else if (s.contains("爻音频")) {

                }else if (s.contains("爻指令")) {

                }else if (s.contains("爻昵称")){
                    chain.append(command.getString("qqName"));
                }else {
                    chain.append(s);
                }
            }
        }
        return chain.build();
    }

    /**
     * 后增强器
     */
    public static void afterReturn (JSONObject singleReturn) {

    }

    /**
     * 处理生入参
     */
    public static String handlerInputKey (String rowRules,JSONObject command,int i) {
       //目前仅处理最末尾数
        if (rowRules.contains("《")) {
            //处理rowRules
            command.put("isNeedAddParams",true);
            return rowRules.substring(0,rowRules.indexOf("《"));

        }else {
            return rowRules;
        }
    }



}
