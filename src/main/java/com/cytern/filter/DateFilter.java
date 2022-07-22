package com.cytern.filter;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotFilter;

import java.util.Date;

/**
 * 日期筛选器
 */
@RobotFilter(name = "时间筛选器")
public class DateFilter {
    private static final int USER_BIRTHDAY = 1; //用户生日
    private static final int MORNING = 1001; //早上 6:00 - 10:00
    private static final int NOON = 1002; //中午 11:00 - 13
    private static final int AFTERNOON = 1003; //下午 14:00 - 17:00
    private static final int EVENING = 1004; //傍晚 17:00 - 19:00
    private static final int NIGHT = 1005; //晚上 19:00 - 4:00


    @RobotFilter(name = "简单时间")
    public static boolean simpleDateFilter(JSONObject baseParams,Integer type) {
        Date date = new Date();
        switch (type) {
            case USER_BIRTHDAY:{
                return false;
            }
            case MORNING:{
                int hour = DateUtil.hour(date, true);
                return hour>=6 && hour<=10;
            }
            case NOON: {
                int hour = DateUtil.hour(date, true);
                return hour>=11 && hour<=13;
            }
            case AFTERNOON:{
                int hour = DateUtil.hour(date, true);
                return hour>=14 && hour<=17;
            }
            case EVENING:{
                int hour = DateUtil.hour(date, true);
                return hour>=17 && hour<=19;
            }
            case NIGHT:{
                int hour = DateUtil.hour(date, true);
                return hour>=19 || hour<=4;
            }
        }
        return false;
    }
}
