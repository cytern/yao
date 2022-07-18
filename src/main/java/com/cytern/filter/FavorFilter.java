package com.cytern.filter;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotFilter;

/**
 * 好感度筛选器
 */
public class FavorFilter {

    @RobotFilter(name = "最小好感度")
    public static boolean minFavorFilter(JSONObject baseParams, String min) {
        System.out.println("here");
         return true;
    }

    @RobotFilter(name = "好感度区间")
    public static boolean minMaxFavorFilter(JSONObject baseParams, String min,String max) {
        return false;
    }
}
