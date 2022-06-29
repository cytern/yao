package com.cytern.command;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotCommand;

@RobotCommand(activeService = "ITouchCommand")
public class ITouchCommand implements RobotCommands{
    @Override
    public JSONObject executedCommand(JSONObject params) {
        return null;
    }
}
