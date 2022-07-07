package com.cytern.command;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotCommand;

/**
 * 基础消息服务
 */
public class CommonIOCommand {

    /**
     * 最基本的消息服务
     */
    @RobotCommand(activeService = "通用消息")
    public static JSONObject baseCommandHandler (JSONObject params) {
        return null;
    }
}
