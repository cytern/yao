package com.cytern.filter;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotFilter;

/**
 * 日期筛选器
 */
public class DateFilter {
    private static final Integer USER_BIRTHDAY = 1; //用户生日
    private static final Integer MORNING = 1001; //早上 6:00 - 11:00
    private static final Integer NOON = 1002; //中午 11:00 - 13:30
    private static final Integer AFTERNOON = 1003; //下午 13:30 - 17:00
    private static final Integer EVENING = 1004; //傍晚 17:00 - 19:00
    private static final Integer NIGHT = 1005; //晚上 19:00 - 4:00


    @RobotFilter(name = "简单时间")
    public static boolean simpleDateFilter(JSONObject baseParams,Integer type) {
        return false;
    }
}
