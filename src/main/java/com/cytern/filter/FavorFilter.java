package com.cytern.filter;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotFilter;

/**
 * 好感度筛选器
 */
public class FavorFilter {

    @RobotFilter(serviceName = "最小好感度")
    public static boolean minFavorFilter(JSONObject baseParams, Integer min) {
          return false;
    }

    @RobotFilter(serviceName = "好感度区间")
    public static boolean minMaxFavorFilter(JSONObject baseParams, Integer min,Integer max) {
        return false;
    }
}