package com.cytern.service.impl.load;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.HashMap;

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
        System.out.println();
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
