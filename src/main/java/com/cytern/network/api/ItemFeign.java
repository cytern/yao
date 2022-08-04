package com.cytern.network.api;

import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.network.util.CommonHttpUtil;
import com.cytern.pojo.ErrorCode;
import com.cytern.service.impl.load.ConfigLoadService;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ItemFeign {
      private static final String itemUrl = "/openData/commonBusiness";

    public static JSONObject itemFlush(List<JSONObject> itemList) {
        JSONObject params = new JSONObject();
        params.put("businessCode",2);
        params.put("robotCode", ConfigLoadService.getInstance().getDefaultActiveRobot());
        params.put("opType","reflush");
        params.put("itemList",itemList);
        return getReturnData(params);
    }

    @NotNull
    public static JSONObject getReturnData(JSONObject params) {
        JSONObject result = CommonHttpUtil.postHttpService(itemUrl, params);
        if (result != null && result.getInteger("code").equals(200)) {
            return result;
        }else if (result== null || !result.containsKey("code")){
            throw new RobotException(ErrorCode.NET_WORK_RETURN_ERROR.getMsg(2));
        }else {
            throw new RobotException(result.getInteger("code"),result.getString("message"));
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
        return getReturnData(params);
    }

    public static JSONObject itemAddOne(String name,String num,String qqId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("itemName",name);
        jsonObject.put("num",num);
        return  itemUpdate(Collections.singletonList(jsonObject),qqId);
    }


    /**
     * 物品使用
     * @param itemList
     * @param qqId
     * @return
     */
    public static JSONObject itemReduce(List<JSONObject> itemList,String qqId) {
        JSONObject params = new JSONObject();
        params.put("businessCode",2);
        params.put("robotCode", ConfigLoadService.getInstance().getDefaultActiveRobot());
        params.put("opType","reduce");
        params.put("itemList",itemList);
        params.put("qqId",qqId);
        return getReturnData(params);
    }
}
