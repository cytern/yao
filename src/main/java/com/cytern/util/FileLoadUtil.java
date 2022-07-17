package com.cytern.util;

import kotlin.io.FileTreeWalk;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Files;

/**
 * 文件加载工具类
 */
public class FileLoadUtil {
    public static final String userDir;
    public static final String modsUrl;
    static {
        if (true) {
            String property = System.getProperty("user.dir");
            userDir = property + "/src/main/resources/mods";
        }else {
            userDir = System.getProperty("user.dir");
        }
        File file = new File(userDir);
        File parentFile = file.getParentFile();
        File[] mods = parentFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().equals("mods");
            }
        });
        if (mods == null || mods.length<1) {
            String newPath = parentFile.getPath() + "\\mods";
            File newFile = new File(newPath);
            newFile.mkdir();
            modsUrl = newFile.getPath();
        }else {
            modsUrl = mods[0].getPath();
        }

    }
}
