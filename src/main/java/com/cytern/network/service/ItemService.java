package com.cytern.network.service;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.cytern.network.api.ItemFeign;

import java.util.List;

public class ItemService {

    public static void syncReflushItems(List<JSONObject> itemList){
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                ItemFeign.itemFlush(itemList);
            }
        });
    }
}