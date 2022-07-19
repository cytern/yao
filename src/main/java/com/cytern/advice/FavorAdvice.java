package com.cytern.advice;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotAdvice;
import com.cytern.network.service.FavorFeign;


public class FavorAdvice {
    @RobotAdvice(name = "好感度增加")
    public static JSONObject favorFilter(JSONObject command,String addNum) {
        JSONObject jsonObject = new JSONObject();
        Integer level = FavorFeign.getFavor(Integer.valueOf(addNum), command.getString("qqId"));
        jsonObject.put("好感度",level);
        return jsonObject;
    }
}
