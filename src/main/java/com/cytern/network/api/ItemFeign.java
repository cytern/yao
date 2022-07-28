package com.cytern.network.api;

import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.network.util.CommonHttpUtil;
import com.cytern.pojo.ErrorCode;
import com.cytern.service.impl.load.ConfigLoadService;

import java.util.List;

public class ItemFeign {
      private static final String itemUrl = "/openData/commonBusiness";

    public static JSONObject itemFlush(List<JSONObject> itemList) {
        JSONObject params = new JSONObject();
        params.put("businessCode",2);
        params.put("robotCode", ConfigLoadService.getInstance().getDefaultActiveRobot());
        params.put("opType","reflush");
        params.put("itemList",itemList);
        JSONObject result = CommonHttpUtil.postHttpService(itemUrl, params);
        if (result != null && result.getInteger("code").equals(200)) {
            return result;
        }else {
            throw new RobotException(ErrorCode.NET_WORK_RETURN_ERROR.getMsg(2));
        }
    }

    /**
     * 物品更新
     * @param itemList
     * @param qqId
     * @return
     */
    public static JSONObject itemUpdate(List<JSONObject> itemList,String qqId) {
        JSONObject params = new JSONObject();
        params.put("businessCode",2);
        params.put("robotCode", ConfigLoadService.getInstance().getDefaultActiveRobot());
        params.put("opType","update");
        params.put("itemList",itemList);
        params.put("qqId",qqId);
        JSONObject result = CommonHttpUtil.postHttpService(itemUrl, params);
        if (result != null && result.getInteger("code").equals(200)) {
            return result;
        }else {
            throw new RobotException(ErrorCode.NET_WORK_RETURN_ERROR.getMsg(2));
        }
    }
}
