package com.cytern.util;

import com.alibaba.fastjson.JSONObject;
import com.cytern.Plugin;
import com.cytern.service.impl.ModLoadService;

import java.util.HashMap;

public class StartMiraiUtil {

    public static void main(String[] args) {
        ModLoadService instance = ModLoadService.getInstance();
        HashMap<String, HashMap<String, JSONObject>> mods = instance.getMods();

    }
}
