package com.cytern.advice;

import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotAdvice;
import com.cytern.exception.RobotException;
import com.cytern.network.service.ItemService;
import com.cytern.pojo.ErrorCode;
import com.cytern.service.impl.load.ItemLoadService;
import com.cytern.util.RobotCachedUtil;
import com.tencentcloudapi.ame.v20190916.models.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;


@RobotAdvice(name = "扭蛋增强器")
public class GashaponAdvice {

    @RobotAdvice(name = "随机扭蛋")
    public static JSONObject randomGashapon (JSONObject params,String times,String type) {
       return getReturnMsg(params,times,type);
    }

    @RobotAdvice(name = "随机扭蛋")
    public static JSONObject randomGashapon (JSONObject params,String times) {
       return getReturnMsg(params,times,null);
    }

    @RobotAdvice(name = "我的物品列表")
    public static JSONObject getItemIHave(JSONObject params) {
        JSONObject backJson = new JSONObject();
        return backJson;
    }

    public static void main(String[] args) {
        ItemLoadService.getInstance().getItemMap();
        JSONObject params = new JSONObject();
        params.put("qqId","2477185748");
        JSONObject jsonObject = randomGashapon(params, "10");
        System.out.println(jsonObject.getString("itemNames"));
    }
















    private static JSONObject getReturnMsg(JSONObject params,String times,String type) {
        int i ;
        try {
            i = Integer.parseInt(times);
        }catch (NumberFormatException formatException) {
            throw new RobotException(ErrorCode.NUM_FOR_ERROR.getMsg(1));
        }
        JSONObject returnJsons = new JSONObject();
        StringBuilder stringBuilder = new StringBuilder();
        List<JSONObject> randomGasShapon = getRandomGasShapon(times, type);
        for (JSONObject jsonObject : randomGasShapon) {
            jsonObject.put("num",1);
            stringBuilder.append(jsonObject.getString("itemName")).append(" ").append("\n");
        }
        returnJsons.put("itemList",randomGasShapon);
        returnJsons.put("times",i);
        returnJsons.put("itemNames",stringBuilder.toString());
        ItemService.updateReflusItems(randomGasShapon,params.getString("qqId"));
       return returnJsons;



    }

    private static List<JSONObject> getRandomGasShapon(String times, String type) {
         //先根据类别筛选一下
        ArrayList<JSONObject> canRandomArrays = new ArrayList<>();
        ArrayList<JSONObject> forceRandomArrays = new ArrayList<>();
        AtomicReference<Integer> totalWeight = new AtomicReference<>(0);
        JSONObject oneTypeCached =null;
        if (type != null) {
            oneTypeCached = RobotCachedUtil.getInstance().getTypeItemCache().get(type);
        }
        if (oneTypeCached != null && !oneTypeCached.isEmpty()) {
            canRandomArrays = (ArrayList<JSONObject>) oneTypeCached.get("canRandomArrays");
            forceRandomArrays = (ArrayList<JSONObject>) oneTypeCached.get("forceRandomArrays");
            totalWeight.set(oneTypeCached.getInteger("totalWeight"));
        }else {
            HashMap<String, JSONObject> itemMap = ItemLoadService.getInstance().getItemMap();
            ArrayList<JSONObject> finalForceRandomArrays = forceRandomArrays;
            ArrayList<JSONObject> finalCanRandomArrays = canRandomArrays;
            itemMap.forEach((key, value) -> {
                if ((value.getString("itemType") != null && value.getString("itemType").equals(type)) || type == null) {
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
                                    totalWeight.set(totalWeight.get() + randomWeight);
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
            onesItem.put("totalWeight",totalWeight.get());
            //存入缓存中
//            RobotCachedUtil.getInstance().getTypeItemCache().put(type,onesItem);
        }
        ArrayList<JSONObject> waitAddItem = new ArrayList<>();
        try {
            int i = Integer.parseInt(times);
            for (int j = 0; j < i; j++) {
                boolean forceFlag = false;
                //通过随机 先去随机强制获取的
                for (JSONObject singleRandom : forceRandomArrays) {
                    JSONObject itemPop = singleRandom.getJSONObject("itemPop");
                    Integer forceChance = itemPop.getInteger("forceChance");
                    int thisOne = RandomUtil.randomInt(100);
                    if (thisOne<forceChance) {
                        if (!singleRandom.getString("itemType").equals("空")) {
                            waitAddItem.add(new JSONObject(singleRandom));
                        }
                        forceFlag = true;
                        break;
                    }
                }
                //加过了就不加了
                if (forceFlag) {
                    continue;
                }
                int ran = RandomUtil.randomInt(totalWeight.get());
                int tempWeight = 0;
                for (int a = 0; a < canRandomArrays.size(); a++) {
                    JSONObject singleRandomItem = canRandomArrays.get(a);
                    if (a == canRandomArrays.size()-1) {
                        if (!singleRandomItem.getString("itemType").equals("空")) {
                            waitAddItem.add(new JSONObject(singleRandomItem));
                        }
                        break;
                    }else {
                        Integer randomWeight = singleRandomItem.getJSONObject("itemPop").getInteger("randomWeight");
                        tempWeight = tempWeight + randomWeight;
                        if (tempWeight>=ran) {
                            if (!singleRandomItem.getString("itemType").equals("空")) {
                                waitAddItem.add(new JSONObject(singleRandomItem));
                            }
                            break;
                        }
                    }
                }


            }
        } catch (NumberFormatException e) {
           throw new RobotException(e);
        }
        //到此步已经获取全部的随机扭蛋
       return waitAddItem;
    }

}
