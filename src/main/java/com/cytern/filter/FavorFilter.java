package com.cytern.filter;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotFilter;
import com.cytern.network.api.FavorFeign;
import com.cytern.network.service.FavorService;

/**
 * 好感度筛选器
 */
@RobotFilter(name = "好感度筛选器")
public class FavorFilter {

    @RobotFilter(name = "最小好感度")
    public static boolean minFavorFilter(JSONObject baseParams, String min) {
        Integer level = FavorService.getFavor(0, baseParams.getString("qqId"));
        return level >= Integer.parseInt(min);
    }

    @RobotFilter(name = "好感度区间")
    public static boolean minMaxFavorFilter(JSONObject baseParams, String min,String max) {
        Integer level = FavorService.getFavor(0, baseParams.getString("qqId"));
        return level >= Integer.parseInt(min) && level < Integer.parseInt(max);
    }
}
