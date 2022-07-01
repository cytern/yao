package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.LoggerService;

import java.util.ArrayList;
import java.util.HashMap;

public class ModCheckService {
    private static volatile ModCheckService modCheckService;

    private ModCheckService(){
        HashMap<String, HashMap<String, JSONObject>> mods = ModLoadService.getInstance().getMods();
        HashMap<String, HashMap<String, String>> assets = ModLoadService.getInstance().getAssets();
        if (!mods.containsKey("configLoader") || !mods.get("configLoader").containsKey("Core")){
            LoggerService.error("无法正确解析MOD 【Core】 将不会加载后续mod 插件无法正常启动");
            mods.clear();
        }
        // TODO 目前设计情况 只需要校验 robotLoader下的robot 即可
        HashMap<String, JSONObject> robotLoader = mods.get("robotLoader");
        ArrayList<String> removeKey = new ArrayList<>();
        robotLoader.forEach((key,value) -> {
            if (!value.containsKey("config")) {
                LoggerService.error("模组 【" + key +"】 无法被加载 缺少配置文件");
                removeKey.add(key);
            };
            JSONArray relyOnMods = value.getJSONArray("relyOnMods");
            if (relyOnMods == null || relyOnMods.size()<1){
                return;
            }
            for (int i = 0; i < relyOnMods.size(); i++) {
                JSONObject single = relyOnMods.getJSONObject(i);
                try {
                    if (!assets.containsKey(single.getString("modCode"))) {
                        LoggerService.error("模组 【" + key +"】 无法被加载 缺少依赖模组 " + "【" + single.getString("modCode") + "】");
                        removeKey.add(key);
                    }
                } catch (Exception e) {
                    removeKey.add(key);
                    LoggerService.error("模组 【" + key +"】 无法被加载 错误的配置文件 " + e);
                }
            }

        });
        //移出key
        for (String s : removeKey) {
            robotLoader.remove(s);
        }
        mods.put("robotLoader",robotLoader);

    }

    public static ModCheckService getInstance() {
        if (modCheckService == null) {
            synchronized (ModCheckService.class) {
                if (modCheckService == null) {
                    modCheckService = new ModCheckService();
                }
            }
        }
        return modCheckService;
    }
}
