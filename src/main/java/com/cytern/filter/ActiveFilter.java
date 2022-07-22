package com.cytern.filter;


import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotFilter;
import com.cytern.service.impl.load.ConfigLoadService;

@RobotFilter(name = "主人筛选器")
public class ActiveFilter {

    @RobotFilter(name = "是否机器人主人")
    public static boolean isRobotMaster(JSONObject baseParams) {
        String masterQqId = ConfigLoadService.getInstance().getMasterQqId();
        String qqId = baseParams.getString("qqId");
        return masterQqId.equals(qqId);
    }
}
