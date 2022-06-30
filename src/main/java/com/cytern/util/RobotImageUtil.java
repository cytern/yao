package com.cytern.util;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import com.cytern.pojo.SimpleImageSub;

import java.io.File;
import java.util.List;

/**
 * 图片工具类
 */
public class RobotImageUtil {
    public static List<String> subImageList(File sourceImage, SimpleImageSub simpleImageSub) {
        //获取sourceImage的位置
        String parentDir = sourceImage.getParentFile().getPath();
        parentDir = parentDir  + "/unLoad";
        //生成解压文件夹
        File unLoadDir = new File(parentDir);
        try {
                unLoadDir.deleteOnExit();
            unLoadDir.mkdirs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImgUtil.sliceByRowsAndCols(sourceImage, unLoadDir,simpleImageSub.getxNum(),simpleImageSub.getyNum());
        //给所有目录下的文件改名字 并且把完整目录返回
        return null;

    }

    public static void main(String[] args) {
         RobotImageUtil.subImageList(FileUtil.file("E:\\projects\\javaProject\\dadi\\mods\\helly\\assets\\Gunther.png"),new SimpleImageSub(2,2));
    }
}
