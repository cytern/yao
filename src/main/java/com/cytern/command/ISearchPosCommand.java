package com.cytern.command;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotCommand;


public class ISearchPosCommand {
    @RobotCommand(activeService = "ISearchPosCommand")
    public JSONObject executedCommand(JSONObject params) {
        return null;
    }
}
