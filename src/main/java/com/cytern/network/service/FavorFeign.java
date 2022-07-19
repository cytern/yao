package com.cytern.network.service;

import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.network.util.CommonHttpUtil;
import com.cytern.service.impl.load.ConfigLoadService;

/**
 * @author 24771
 */
public class FavorFeign {
    private static final String favorUrl = "/openData/commonBusiness";


    public static JSONObject favorEdit(Integer level,String qqId) {
        JSONObject params = new JSONObject();
        params.put("businessCode",1);
        params.put("robotCode", ConfigLoadService.getInstance().getConfig().get("defaultActiveRobot"));
        params.put("qqId",qqId);
        params.put("level",level);
        params.put("opType","update");
        JSONObject result = CommonHttpUtil.postHttpService(favorUrl, params);
        if (result != null && result.getBoolean("code")) {
           return result;
        }else {
            throw new RobotException("无效的网络返回值 1001");
        }
    }

    public static Integer getFavor(Integer level,String qqId) {
        JSONObject result = favorEdit(level, qqId);
        return result.getJSONObject("data").getInteger("level");
    }

}
