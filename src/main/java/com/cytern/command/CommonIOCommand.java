package com.cytern.command;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotCommand;
import com.cytern.service.impl.CommandExecutedService;

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
        JSONObject repeatFilterReturn = CommandExecutedService.repeatFilterReturn(preReturns);
        JSONObject newCommand = CommandExecutedService.preReturn(commands, repeatFilterReturn);
        String msg = CommandExecutedService.handleReturnMsg(newCommand, repeatFilterReturn);

        return null;
    }
}
