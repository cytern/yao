package com.cytern.network.api;

import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.network.util.CommonHttpUtil;
import com.cytern.pojo.ErrorCode;
import com.cytern.service.impl.load.ConfigLoadService;

/**
 * @author 24771
 */
public class FavorFeign {
    private static final String favorUrl = "/openData/commonBusiness";


    public static JSONObject favorEdit(Integer level,String qqId) {
        JSONObject params = new JSONObject();
        params.put("businessCode",1);
        params.put("robotCode", ConfigLoadService.getInstance().getDefaultActiveRobot());
        params.put("qqId",qqId);
        params.put("level",level);
        params.put("opType","update");
        JSONObject result = CommonHttpUtil.postHttpService(favorUrl, params);
        if (result != null && result.getInteger("code").equals(200)) {
           return result;
        }else {
            throw new RobotException(ErrorCode.NET_WORK_RETURN_ERROR.getMsg(1));
        }
    }



}
