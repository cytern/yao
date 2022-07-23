package com.cytern.advice;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotAdvice;
import com.cytern.network.api.FavorFeign;
import com.cytern.network.service.FavorService;
import com.cytern.util.MathUtil;

@RobotAdvice(name = "好感度增强器")
public class FavorAdvice {
    @RobotAdvice(name = "好感度增加")
    public static JSONObject favorFilter(JSONObject command,String addNum) {
        JSONObject jsonObject = new JSONObject();
        Integer level = FavorService.getFavor(Integer.valueOf(addNum), command.getString("qqId"));
        jsonObject.put("好感度",level);
        return jsonObject;
    }

    /**
     * @param factor 分布因子
     * @return
     */
    @RobotAdvice(name = "好感度随机增加")
    public static JSONObject favorFilterRandom(JSONObject command,String min,String max,String factor) {
        JSONObject jsonObject = new JSONObject();
        Integer minNum = Integer.valueOf(min);
        Integer maxNum = Integer.valueOf(max);
        Double aDouble = Double.valueOf(factor);
        Integer tempLevel = MathUtil.randomFactorInt(minNum, maxNum, aDouble);
        Integer level = FavorService.getFavor(tempLevel, command.getString("qqId"));
        jsonObject.put("好感度",level);
        return jsonObject;
    }
}
