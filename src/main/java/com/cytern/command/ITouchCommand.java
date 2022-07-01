package com.cytern.command;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotCommand;

public class ITouchCommand{

    @RobotCommand(activeService = "ITouchCommand")
    public JSONObject executedCommand(JSONObject params) {
        return null;
    }
}
