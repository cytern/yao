package com.cytern.command;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotCommand;
import com.cytern.service.impl.CommandExecutedService;
import com.cytern.util.MessageSenderUtil;
import net.mamoe.mirai.contact.Contact;

/**
 * 基础消息服务
 */
public class CommonIOCommand {

    /**
     * 最基本的消息服务
     */
    @RobotCommand(activeService = "通用消息")
    public static JSONObject baseCommandHandler (JSONObject commands) {
        //基本消息服务应该分以下几步走
        JSONArray preReturns = CommandExecutedService.preFilterReturn(commands);
        JSONObject repeatFilterReturn = CommandExecutedService.repeatFilterReturn(preReturns,commands);
        JSONObject newCommand = CommandExecutedService.preReturn(repeatFilterReturn, null);
        String msg = CommandExecutedService.handleReturnMsg(newCommand);
        System.out.println(msg);
        MessageSenderUtil.normalSend( (Contact) commands.get("subject"),msg);
        return null;
    }
}
