package com.cytern.filter;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotFilter;
import com.cytern.service.impl.LoggerService;

import java.util.jar.JarEntry;
import java.util.logging.Logger;

@RobotFilter(name = "基础通用筛选器")
public class CommonFilter {
    @RobotFilter(name = "相等筛选")
    public static boolean equalsFilter(JSONObject params,String arg1,String arg2) {
        LoggerService.info(arg1);
        LoggerService.info(arg2);
        return arg1.equals(arg2);
    }


    @RobotFilter(name = "区间筛选")
    public static boolean betweenFiler(JSONObject params,String min,String data,String max) {
        try {
            int maxNum = Integer.parseInt(max);
            int dataNum = Integer.parseInt(data);
            int minNum = Integer.parseInt(min);
            return minNum<= dataNum && dataNum< maxNum;
        } catch (NumberFormatException e) {
           return false;
        }
    }

    @RobotFilter(name = "大于筛选")
    public static boolean biggerFiler(JSONObject params,String min,String data) {
        try {
            int dataNum = Integer.parseInt(data);
            int minNum = Integer.parseInt(min);
            return minNum<= dataNum;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @RobotFilter(name = "小于筛选")
    public static boolean minFiler(JSONObject params,String max,String data) {
        try {
            int maxNum = Integer.parseInt(max);
            int dataNum = Integer.parseInt(data);
            return dataNum< maxNum;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
