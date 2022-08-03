package com.cytern.advice;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.aspect.RobotAdvice;
import com.cytern.exception.RobotException;
import com.cytern.network.api.ItemFeign;
import com.cytern.network.service.ItemService;
import com.cytern.pojo.ErrorCode;
import com.cytern.service.impl.LoggerService;
import com.cytern.service.impl.load.ConfigLoadService;
import com.cytern.service.impl.load.ItemLoadService;
import com.cytern.service.impl.load.base.AssetsUnzipLoadService;
import com.cytern.util.MessageSenderUtil;
import com.cytern.util.RobotCachedUtil;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.message.data.MessageChainBuilder;

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
        StringBuilder stringBuilder = new StringBuilder();
        JSONObject items = ItemFeign.itemUpdate(null, params.getString("qqId"));
        JSONArray itemList = items.getJSONObject("data").getJSONArray("itemList");
        for (int i = 0; i < itemList.size(); i++) {
            JSONObject jsonObject = itemList.getJSONObject(i);
            stringBuilder.append(jsonObject.getString("itemName")).append(" ").append("*").append(jsonObject.getString("num")).append("\n");
        }
        backJson.put("itemList",itemList);
        backJson.put("物品栏",stringBuilder.toString());
        return backJson;
    }

    @RobotAdvice(name = "检视物品")
    public static JSONObject showItemWhat (JSONObject params,String itemName) {
        JSONObject backJson = new JSONObject();
        HashMap<String, JSONObject> itemMap = ItemLoadService.getInstance().getItemMap();
        StringBuilder stringBuilder = new StringBuilder();
        AtomicReference<JSONObject> singleJson = new AtomicReference<>(new JSONObject());
        itemMap.forEach( (key,value) -> {
            if (value.getString("itemName").equals(itemName)) {
                 singleJson.set(value);
            }
        });
        JSONObject finalJson = singleJson.get();
        if (finalJson.isEmpty()) {
            MessageChainBuilder chain = new MessageChainBuilder();
            chain.append("看不见！").append(ConfigLoadService.getInstance().getDefaultRobotName()).append("从来没见过那个哎");
            MessageSenderUtil.normalSend( (Contact) params.get("subject"),chain.build());
            throw new RobotException("找不到指定物品");
        }
        backJson.put("物品图片",finalJson.getString("itemImgName"));
        stringBuilder.append("物品名称:").append(finalJson.getString("itemName")).append("\n")
                .append("物品类别:").append(finalJson.getString("itemType")).append("\n")
                .append("物品介绍:").append(finalJson.getString("itemDes")).append("\n");
        backJson.put("物品介绍",stringBuilder.toString());
        return backJson;

    }

    public static void main(String[] args) {
        HashMap<String, HashMap<String, String>> assets = AssetsUnzipLoadService.getInstance().getAssets();
        HashMap<String, JSONObject> itemMap = ItemLoadService.getInstance().getItemMap();
        System.out.println();
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
