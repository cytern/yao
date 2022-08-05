package com.cytern.advice;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotAdvice;
import com.cytern.aspect.RobotFilter;
import com.cytern.exception.RobotException;
import com.cytern.network.api.ItemFeign;
import com.cytern.service.impl.LoggerService;
import com.cytern.util.MessageSenderUtil;
import net.mamoe.mirai.contact.Contact;

import java.util.ArrayList;
import java.util.List;

@RobotAdvice(name = "物品筛选器")
public class ItemAdvice {


    public static JSONObject itemUser(List<JSONObject> items,JSONObject params){
        JSONObject request = new JSONObject();
        request.put("itemList",items);
        JSONObject reduceObject =new JSONObject();
        JSONObject returnData = new JSONObject();
        try {
             reduceObject = ItemFeign.itemReduce(items, params.getString("qqId"));
        } catch (RobotException e) {
            returnData.put("返回结果","失败");
        }
        returnData.put("返回结果","成功");
        return returnData;
    }

    @RobotAdvice(name = "物品消耗")
    public static JSONObject itemUseFilter (JSONObject params,String name,String num) {
        LoggerService.info("进入到物品消耗筛选器");
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("num",num);
        jsonObject.put("itemName",name);
        jsonObjects.add(jsonObject);
        return itemUser(jsonObjects,params);
    }



    @RobotAdvice(name = "物品消耗")
    public static JSONObject itemUseFilter (JSONObject params,String name1,String num1,String name2,String num2) {
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("num",num1);
        jsonObject.put("itemName",name1);
        jsonObjects.add(jsonObject);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("num",num2);
        jsonObject2.put("itemName",name2);
        jsonObjects.add(jsonObject2);
        return itemUser(jsonObjects,params);

    }

    @RobotAdvice(name = "物品消耗")
    public static JSONObject itemUseFilter (JSONObject params,String name1,String num1,String name2,String num2,String name3,String num3) {
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("num",num1);
        jsonObject.put("itemName",name1);
        jsonObjects.add(jsonObject);
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("num",num2);
        jsonObject2.put("itemName",name2);
        jsonObjects.add(jsonObject2);
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("num",num3);
        jsonObject3.put("itemName",name3);
        jsonObjects.add(jsonObject3);
        return itemUser(jsonObjects,params);
    }
}
