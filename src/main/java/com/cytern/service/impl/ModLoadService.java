package com.cytern.service.impl;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.cytern.Plugin;
import com.cytern.util.FileLoadUtil;
import com.cytern.util.RobotFileVisitor;
import kotlinx.serialization.json.JsonObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 模组加载
 */
public class ModLoadService {
    private static volatile ModLoadService modLoadService;
    //模组
    private final HashMap<String,HashMap<String,JSONObject>> mods;
    //静态资源 code : 绝对路径
    private final HashMap<String,String> assets;


    /**
     * 加载mods文件夹下的东西
     */
    private ModLoadService() {
        HashMap<String,HashMap<String,JSONObject>> waitMods = new HashMap<>();
        HashMap<String, String> waitAssets = new HashMap<>();
        try {
            Files.walkFileTree(Paths.get(FileLoadUtil.modsUrl), new RobotFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    //加载json文件
                    loadJsonData(waitMods,file);
                    //TODO 加载素材文件 以key value 对形式
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
       mods = waitMods;
    }

    private void loadJsonData( HashMap<String,HashMap<String,JSONObject>> waitMods,Path file) {
        if (file.getFileName().toString().equals("config.json") ) {
            JSONObject modObject = new JSONObject();
            HashMap<String,JSONObject> sameTypeMods;
            JSONObject configObject = JSONObject.parseObject(FileUtil.readString(file.toFile(), StandardCharsets.UTF_8));
            modObject.put("config",configObject);
            String modCode = configObject.getString("modCode");
            String classLoadType = configObject.getString("classLoadType");
            if (waitMods.containsKey(classLoadType)) {
                sameTypeMods = waitMods.get(classLoadType);
            }else {
                sameTypeMods = new HashMap<>();
            }
            //判断是否有写mod 码
            if (modCode == null || modCode.equals("")) {
               LoggerService.error("无法正确加载mod 原因【无效的modCode】 出错mod：" + file.toFile().getPath());
                return;
            }
            //mod码是否重复
            if (sameTypeMods.containsKey(modCode)) {
                LoggerService.error("无法正确加载mod 原因【重复的modCode】 重复值为: " +  modCode +" " + "出错mod：" + file.toString());
                return;
            }
            //是否有main.json 主要数据文件
            File[] files = file.getParent().toFile().listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().equals("main.json");
                }
            });
            if (files == null || files.length != 1) {
                LoggerService.error("无法正确加载mod 原因【未正确配置main.json文件】 出错mod：" + file.toString());
                return;
            }
            File mainData = files[0];
            JSONObject mainObject = JSONObject.parseObject(FileUtil.readString(mainData, StandardCharsets.UTF_8));
            modObject.put("mian",mainObject);
            modObject.put("baseUrl",file.toFile().getParentFile().getPath());
            sameTypeMods.put(modCode,modObject);
            LoggerService.info("mod LoadSuccess:  " + file.getParent().toString());
            waitMods.put(classLoadType,sameTypeMods);
        }

    }

    private void loadAssetsData (JSONObject modObject,Path file) {
        //是否有assets 文件夹
        File[] files = file.getParent().toFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().equals("assets");
            }
        });
    }

    public static ModLoadService getInstance( ) {
        if (modLoadService == null) {
            synchronized (ModLoadService.class) {
                if (modLoadService == null) {
                    modLoadService = new ModLoadService();
                }
            }
        }
        return modLoadService;
    }

    public HashMap<String, HashMap<String, JSONObject>> getMods() {
        return mods;
    }

}
