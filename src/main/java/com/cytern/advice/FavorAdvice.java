package com.cytern.advice;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotAdvice;


public class FavorAdvice {
    @RobotAdvice(name = "好感度增加")
    public static JSONObject favorFilter(JSONObject command,String addNum) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("好感度",addNum);
        return jsonObject;
    }
}
