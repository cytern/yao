package com.cytern.advice;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotAdvice;

import java.util.List;


@RobotAdvice(name = "扭蛋增强器")
public class GashaponAdvice {

    @RobotAdvice(name = "随机扭蛋")
    public static JSONObject randomGashapon (String times,String type) {
        return null;
    }

    @RobotAdvice(name = "随机扭蛋")
    public static JSONObject randomGashapon (String times) {
        return null;
    }

    private List<JSONObject> getRandomGasShapon(String times,String type) {
        return null;
    }

}
