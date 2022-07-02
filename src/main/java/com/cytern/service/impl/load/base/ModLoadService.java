package com.cytern.service.impl.load.base;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.LoggerService;
import com.cytern.util.FileLoadUtil;
import com.cytern.util.RobotFileVisitor;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

/**
 * 最基础的模组加载 负责将各个模组的 config main文件加载出来
 */
public class ModLoadService {
    //模组
    protected final HashMap<String,HashMap<String,JSONObject>> mods;
    //静态资源 code : 绝对路径
    protected final HashMap<String,HashMap<String,String>> assets;


    /**
     * 加载mods文件夹下的东西
     */
    public ModLoadService() {
        HashMap<String,HashMap<String,JSONObject>> waitMods = new HashMap<>();
        HashMap<String,HashMap<String,String>> waitAssets = new HashMap<>();
        try {
            Files.walkFileTree(Paths.get(FileLoadUtil.modsUrl), new RobotFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    //加载json文件
                    String modCode = loadJsonData(waitMods, file);
                    //加载素材文件
                    if (modCode != null) {
                        loadAssetsData(waitAssets,file,modCode);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        mods = waitMods;
        assets = waitAssets;
    }


    private String  loadJsonData( HashMap<String,HashMap<String,JSONObject>> waitMods,Path file) {
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
            if (modCode.equals("")) {
               LoggerService.error("无法正确加载mod 原因【无效的modCode】 出错mod：" + file.toFile().getPath());
                return null;
            }
            //mod码是否重复
            if (sameTypeMods.containsKey(modCode)) {
                LoggerService.error("无法正确加载mod 原因【重复的modCode】 重复值为: " +  modCode +" " + "出错mod：" + file.toString());
                return modCode;
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
                return modCode;
            }
            File mainData = files[0];
            JSONObject mainObject = JSONObject.parseObject(FileUtil.readString(mainData, StandardCharsets.UTF_8));
            modObject.put("main",mainObject);
            modObject.put("baseUrl",file.toFile().getParentFile().getPath());
            sameTypeMods.put(modCode,modObject);
            LoggerService.info("mod LoadSuccess:  " + file.getParent().toString());
            waitMods.put(classLoadType,sameTypeMods);
            return modCode;
        }
        return null;

    }

    public static void loadAssetsData (HashMap<String,HashMap<String,String>> waitAssets,Path file,String modCode) {
        if (modCode == null) {
            LoggerService.error("modCode is null " + file.toString());
            return;
        }
        HashMap<String, String> singleMap = waitAssets.getOrDefault(modCode,new HashMap<>());
        //是否有assets 文件夹
        File[] files = file.getParent().toFile().listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().equals("assets");
            }
        });
        if (files != null && files.length ==1) {
            try {
                File assets = files[0];
                Files.walkFileTree(Paths.get(assets.getPath()), new RobotFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        singleMap.put(file.getFileName().toString(),file.toString());
                        return FileVisitResult.CONTINUE;
                    };

                });
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        waitAssets.put(modCode,singleMap);
    }


    public HashMap<String, HashMap<String, JSONObject>> getMods() {
        return mods;
    }

    public HashMap<String,HashMap<String,String>>  getAssets() {
        return assets;
    }
}
