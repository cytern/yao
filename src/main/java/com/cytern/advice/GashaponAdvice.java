package com.cytern.advice;

import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotAdvice;
import com.cytern.service.impl.load.ItemLoadService;
import com.cytern.util.RobotCachedUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RobotAdvice(name = "扭蛋增强器")
public class GashaponAdvice {

    @RobotAdvice(name = "随机扭蛋")
    public static JSONObject randomGashapon (String times,String type) {
        return null;
    }

    @RobotAdvice(name = "随机扭蛋")
    public static JSONObject randomGashapon (String times) {
        return null;
    }

    private List<JSONObject> getRandomGasShapon(String times,String type) {
         //先根据类别筛选一下
        ArrayList<JSONObject> canRandomArrays = new ArrayList<>();
        ArrayList<JSONObject> forceRandomArrays = new ArrayList<>();
        JSONObject oneTypeCached = RobotCachedUtil.getInstance().getTypeItemCache().get(type);
        if (oneTypeCached != null) {
            canRandomArrays = (ArrayList<JSONObject>) oneTypeCached.get("canRandomArrays");
            forceRandomArrays = (ArrayList<JSONObject>) oneTypeCached.get("forceRandomArrays");
        }else {
            HashMap<String, JSONObject> itemMap = ItemLoadService.getInstance().getItemMap();
            ArrayList<JSONObject> finalForceRandomArrays = forceRandomArrays;
            ArrayList<JSONObject> finalCanRandomArrays = canRandomArrays;
            itemMap.forEach((key, value) -> {
                if (value.getString("itemType") != null && value.getString("itemType").equals(type)) {
                    JSONObject itemPop = value.getJSONObject("itemPop");
                    if (itemPop != null && !itemPop.isEmpty()) {
                        if (itemPop.containsKey("forceChance")) {
                            try {
                                Integer forceChance = itemPop.getInteger("forceChance");
                                if (forceChance != null && forceChance != 0) {
                                    finalForceRandomArrays.add(value);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else if (itemPop.containsKey("randomWeight")) {
                            try {
                                Integer randomWeight = itemPop.getInteger("randomWeight");
                                if (randomWeight != null && randomWeight != 0) {
                                    finalCanRandomArrays.add(value);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            JSONObject onesItem = new JSONObject();
            onesItem.put("canRandomArrays",finalCanRandomArrays);
            onesItem.put("forceRandomArrays",finalForceRandomArrays);
            //存入缓存中
            RobotCachedUtil.getInstance().getTypeItemCache().put(type,onesItem);
        }
        try {
            int i = Integer.parseInt(times);
            for (int j = 0; j < i; i++) {
                //通过随机




            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

}
