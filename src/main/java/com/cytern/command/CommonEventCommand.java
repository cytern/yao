package com.cytern.command;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.CommonMessageProcessor;
import com.cytern.aspect.RobotCommand;
import com.cytern.exception.RobotException;
import com.cytern.service.impl.CommandExecutedService;
import com.cytern.service.impl.LoggerService;
import com.cytern.util.MessageSenderUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.List;

@RobotCommand(activeService = "通用事件服务")
public class CommonEventCommand {

    @RobotCommand(activeService = "通用事件")
    public static JSONObject baseCommandHandler (JSONObject commands) {
        //基本事件服务应该分以下几步走
        JSONArray preReturns = null;
        //前置增强器
        commands = CommandExecutedService.preAdvice(commands);
        //处理筛选
        commands = handlerAcceptKey(commands);
        //筛选器
        preReturns = CommandExecutedService.filter(commands);
        JSONObject repeatFilterReturn = null;

        //重复选择器
        try {
            repeatFilterReturn = CommandExecutedService.repeatFilter(preReturns,commands);
        } catch (RobotException e) {
            LoggerService.error(e.getMessage());
        }

        //增强器
        JSONObject newCommand = null;
        newCommand = CommandExecutedService.advice(repeatFilterReturn, null);

        List<MessageChain> msg = null;

        //最终参数处理
        try {
            msg = CommandExecutedService.handleReturnMsg(newCommand);
        } catch (RobotException e) {
            LoggerService.error(e.getMessage());
        }
        MessageSenderUtil.normalSend( (Contact) commands.get("subject"),msg);
        return null;
    }


    public static JSONObject handlerAcceptKey(JSONObject commands) {
        JSONArray returnWordRules = commands.getJSONArray("returnWordRules");
        JSONArray newReturnWordRules = new JSONArray();
        for (int i = 0; i < returnWordRules.size(); i++) {
            JSONObject jsonObject = returnWordRules.getJSONObject(i);
            if (commands.getString("sourceMessage").equals(jsonObject.getString("activeAccept"))) {
                newReturnWordRules.add(jsonObject);
            }
        }
        commands.put("returnWordRules",newReturnWordRules);
        return commands;
    }
}
