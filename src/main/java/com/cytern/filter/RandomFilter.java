package com.cytern.filter;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotFilter;

/**
 * 随机筛选器
 */
@RobotFilter(name = "随机筛选器")
public class RandomFilter {

    @RobotFilter(name = "简单随机")
    public static Integer simpleRandomFilter(JSONObject baseParams,Integer rate) {
       return rate;
    }
}
