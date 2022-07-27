package com.cytern.service.impl.load;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.network.service.ItemService;
import com.cytern.service.impl.load.base.AssetsUnzipLoadService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 加载item 的方法 最终服务方 之一 提供所有的物品属性 和信息
 */
public final class ItemLoadService {
    private final HashMap<String,JSONObject> itemMap;
    private static volatile ItemLoadService itemLoadService;

    private ItemLoadService() {
        HashMap<String, JSONObject> waitItemMap = new HashMap<>();
        HashMap<String, HashMap<String, JSONObject>> mods = AssetsUnzipLoadService.getInstance().getMods();
        HashMap<String, HashMap<String, String>> assets = AssetsUnzipLoadService.getInstance().getAssets();
        System.out.println();
        HashMap<String, JSONObject> itemLoader = mods.get("itemLoader");
        if (itemLoader != null) {
            itemLoader.forEach((key,value) -> {
                JSONObject main = value.getJSONObject("main");
                HashMap<String, String> thisModsAssets = assets.get(key);
                if (main != null) {
                    JSONObject data = main.getJSONObject("data");
                    if (data !=null) {
                        JSONArray items = data.getJSONArray("items");
                        if (items != null && items.size()>0) {
                            for (int i = 0; i < items.size(); i++) {
                                JSONObject singleItem = items.getJSONObject(i);
                                singleItem.put("itemImgName",thisModsAssets.get(singleItem.getString("itemImgName")));
                                waitItemMap.put(key+"." + singleItem.getString("itemName"),singleItem);
                            }
                        }
                    }
                }
            });
        }
        itemMap = waitItemMap;
        ArrayList<JSONObject> itemList = new ArrayList<>();
        itemMap.forEach((key,value) -> {
            itemList.add(value);
        });
        //刷新到服务器中
        ItemService.syncReflushItems(itemList);

    }
    public static ItemLoadService getInstance() {
        if (itemLoadService == null) {
            synchronized (ItemLoadService.class) {
                if (itemLoadService == null) {
                    itemLoadService = new ItemLoadService();
                }
            }
        }
        return itemLoadService;
    }

    public static void main(String[] args) {
        new ItemLoadService();
    }
}
