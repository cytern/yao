package com.cytern.service.impl.load;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.pojo.SimpleImageSub;
import com.cytern.util.RobotImageUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.HashMap;
import java.util.List;

public class AssetsLoadService {
    private static volatile AssetsLoadService assetsLoadService;

    /**
     * 这位大哥是真正的mod资源加载的消费方 他会解析所有的图片文件 如果需要拆分 会进行拆分
     */
    private AssetsLoadService() {
        ModLoadService modLoadService = ModLoadService.getInstance();
        HashMap<String,HashMap<String,String>>  assets = modLoadService.getAssets();
        HashMap<String, HashMap<String, JSONObject>> mods = modLoadService.getMods();
        //去寻找所有 已经注册的mod中 标记为multiple 的图片 进行再解析
        mods.forEach((k,v) -> {
            v.forEach((key,value) -> {
                try {
                    JSONObject config = value.getJSONObject("config");
                    //目前仅加载机器人加载类
                    if (config.getString("classLoadType").equals("robotLoader")){
                        String modCode = config.getString("modCode");
                        JSONArray assetsArray = value.getJSONObject("main").getJSONArray("assets");
                        for (int i = 0; i < assetsArray.size(); i++) {
                            JSONObject singleAssets = assetsArray.getJSONObject(i);
                            JSONObject config1 = singleAssets.getJSONObject("config");
                            if (singleAssets.containsKey("config") && config1.getString("imgType").equals("multiple")) {
                                HashMap<String, String> assetsMap = assets.get(modCode);
                                HashMap<String,String> strings = RobotImageUtil.subImageList(FileUtil.file(assetsMap.get(singleAssets.getString("fileName"))), new SimpleImageSub(config1.getInteger("row"), config1.getInteger("col")));
                                assetsMap.putAll(strings);
                                assets.put(modCode,assetsMap);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public static AssetsLoadService getInstance() {
        if (assetsLoadService == null) {
            synchronized (AssetsLoadService.class) {
                if (assetsLoadService == null) {
                    assetsLoadService = new AssetsLoadService();
                }
            }
        }
        return assetsLoadService;
    }

    public static void main(String[] args) {
        AssetsLoadService assetsLoadService = new AssetsLoadService();
    }
}
