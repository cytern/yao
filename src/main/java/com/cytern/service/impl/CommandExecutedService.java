package com.cytern.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.pojo.ErrorCode;
import com.cytern.service.impl.load.AdviceLoadService;
import com.cytern.service.impl.load.FilterLoadService;
import com.cytern.service.impl.load.RobotLoadService;
import com.cytern.service.impl.load.base.RobotCommandLoadService;
import com.cytern.util.CommCodeResultUtil;
import com.cytern.util.MessageSenderUtil;
import com.cytern.util.RobotCachedUtil;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * 处理原生语句
     */
    public static String handlerRawStringToType (String[] params,String base) {
        StringBuilder finalString = new StringBuilder(base);
        for (int i = 0; i < params.length; i++) {
            String param = params[i];
            if (params.length == 1 && !params[0].equals("")) {
                finalString.append("(").append("String").append(")");
            } else if (params.length == 1) {
                finalString.append("(").append(")");
            } else if (i == 0) {
                finalString.append("(");
                finalString.append("String");
            } else if (i == params.length - 1) {
                finalString.append(",String)");
            } else {
                finalString.append(",String");
            }
        }
        return finalString.toString();
    }

    /**
     * 重复筛选器
     */
    public static JSONObject repeatFilter(JSONArray returnWordRules, JSONObject baseCommand) {
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
            if (repeatFilter == null|| repeatFilter.size() <1) {
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
     * 增强器
     */
    public static JSONObject preAdvice(JSONObject command) {
        JSONArray preReturn = command.getJSONArray("preAdvice");
        if (preReturn != null && preReturn.size()>0) {
            for (int i = 0; i < preReturn.size(); i++) {
                String rawString = preReturn.getString(i);
                String substring = rawString.substring(rawString.indexOf("(")+1, rawString.indexOf(")"));
                String[] params = substring.split(",");
                resetParams(params,command);
                try {
                    command = AdviceLoadService.getInstance().handlerPreAdviceExecuted(command,handlerRawStringToType(params, rawString.substring(0, rawString.indexOf("("))), params,i);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    throw new RobotException("系统错误");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RobotException("系统错误");
                }
            }
        }
        return command;

    }

    /**
     * 增强器
     */
    public static JSONObject advice(JSONObject command, JSONObject singleReturn) {
        singleReturn = command.getJSONObject("finalReturn");
        JSONArray preReturn = singleReturn.getJSONArray("advice");
        if (preReturn != null) {
            for (int i = 0; i < preReturn.size(); i++) {
                String rawString = preReturn.getString(i);
                String substring = rawString.substring(rawString.indexOf("(")+1, rawString.indexOf(")"));
                String[] params = substring.split(",");
                resetParams(params,command);
                try {
                    command = AdviceLoadService.getInstance().handlerAdviceExecuted(command,handlerRawStringToType(params, rawString.substring(0, rawString.indexOf("("))), params,i);
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    throw new RobotException("系统错误");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RobotException("系统错误");
                }
            }
        }
        return command;

    }


    /**
     * 处理内置入参
     * @param params
     * @param commands
     */
    private static void resetParams (String[] params,JSONObject commands) {
        for (int i = 0; i < params.length; i++) {
            params[i] = CommCodeResultUtil.getFinalParams(commands,params[i]);
        }
    }



    /**
     * 筛选器
     */
    public static JSONArray filter(JSONObject command) {
        JSONArray returnWordRules = command.getJSONArray("returnWordRules");
        if (returnWordRules.size()<1) {
            throw new RobotException("无效的机器编码 无法获取到任何一个可达到的回复");
        }
        JSONArray returns = new JSONArray();
        for (int i = 0; i < returnWordRules.size(); i++) {
            JSONArray unPassArray = new JSONArray();
            JSONObject singleReturn = returnWordRules.getJSONObject(i);
            JSONArray preFilter = singleReturn.getJSONArray("filter");
            if (preFilter!= null && preFilter.size()>0) {
                for (int j = 0; j < preFilter.size(); j++) {
                    String rawString = preFilter.getString(j);
                    if (rawString.equals("")) {
                        continue;
                    }
                    String substring = rawString.substring(rawString.indexOf("(")+1, rawString.indexOf(")"));
                    String[] params = substring.split(",");
                    //重新处理入参
                    resetParams(params,command);
                    boolean b = FilterLoadService.getInstance().handlerFilterExecuted(command, handlerRawStringToType(params, rawString.substring(0, rawString.indexOf("("))), params);
                    LoggerService.info("比较器执行结果 " + b);
                    if (!b) {
                        unPassArray.add(preFilter);
                        break;
                    }
                }
                if (unPassArray.size() == 0) {
                    returns.add(singleReturn);
                }
            }else {
                returns.add(singleReturn);
            }
        }
        if (returns.size()<1) {
            throw new RobotException("不满足筛选器条件");
        }
        return returns;
    }



    /**
     * 处理返回消息
     */
    public static List<MessageChain> handleReturnMsg(JSONObject command) {
        JSONObject finalReturn = command.getJSONObject("finalReturn");
        ArrayList<MessageChain> messageChains = new ArrayList<>();
        String returnMsg = finalReturn.getString("returnMsg");
        MessageChainBuilder chain = new MessageChainBuilder();
        if (!returnMsg.contains("《") && !returnMsg.contains("》")) {
            return Collections.singletonList(chain.append(returnMsg).build());
        }else {
            String[] split = returnMsg.split("\\《|\\》");
            for (int i = 0; i < split.length; i++) {
                String s = split[i];
                if (s.contains("爻服务") || s.contains("爻入参") || s.contains("爻前置")) {
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
                                  String fianlString = tempObj.getString(s1);
                                  if (fianlString.contains("爻图片")) {
                                      String miraiCode = MessageSenderUtil.uploadAndReplaceImage(fianlString, (Bot) command.get("currentBot"));
                                      chain.append(Image.fromId(miraiCode));
                                  }else {
                                      chain.append(fianlString);
                                  }
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
                }else if (s.contains("爻分隔")){
                    messageChains.add(chain.build());
                    chain = new MessageChainBuilder();
                }else if (s.contains("爻事件")){
                    if (s.contains(".")) {
                        String[] split1 = s.split("\\.");
                        if (split1.length == 2) {
                            JSONObject jsonObject = RobotLoadService.getInstance().getRobotEvent().get(split1[1]);
                            if (jsonObject != null && !jsonObject.isEmpty()) {
                                RobotCachedUtil.getInstance().getEventCache().put(command.getString("qqId"),jsonObject);
                            }
                        }
                    }
                }

                else {
                    chain.append(s);
                }
            }
        }
        messageChains.add(chain.build());
        return messageChains;
//        return null;
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

    public static boolean isOtherKey(String rowRules) {
        return rowRules.contains("《")&&rowRules.contains("》");
    }



}
