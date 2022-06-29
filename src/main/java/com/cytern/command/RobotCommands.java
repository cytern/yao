package com.cytern.command;

import com.alibaba.fastjson.JSONObject;

public interface RobotCommands {
    JSONObject executedCommand(JSONObject params);
}
