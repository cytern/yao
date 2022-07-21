package com.cytern.util;

import cn.hutool.core.io.FileUtil;
import com.cytern.Plugin;
import com.cytern.exception.RobotException;
import com.cytern.pojo.ErrorCode;
import com.cytern.service.impl.load.base.AssetsUnzipLoadService;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.util.HashMap;

/**
 * 信息发送工具
 */
public class MessageSenderUtil {

    /**
     * 基础信息发送
     */
    public static void normalSend(Contact subject,String finalMsg) {
        subject.sendMessage(finalMsg);
    }

    /**
     * 处理mirai码
     */
    public static String uploadAndReplaceImage(String rawCode, Bot currentRobot) {
        //查看图片缓存是否有缓存的imageId
        String imageId = TimedCachedUtil.getInstance().getImageCache().get(rawCode);
        if (imageId != null) {
            return  "[mirai:image:" + imageId + "]";
        }else {
            //从资产中获取
            String[] split = rawCode.split("\\.");
            if (split.length != 3) {
                throw new RobotException(ErrorCode.IMAGE_UPLOAD_CANT_FOUND_SOURCE.getMsg());
            }
            HashMap<String, HashMap<String, String>> assets = AssetsUnzipLoadService.getInstance().getAssets();
            HashMap<String, String> stringStringHashMap = assets.get(split[1]);
            if (stringStringHashMap == null) {
                throw new RobotException(ErrorCode.IMAGE_UPLOAD_CANT_FOUND_PATH.getMsg());
            }
            String finalPath = stringStringHashMap.get(split[2]);
            File file = FileUtil.file(finalPath);
            if (!file.exists()) {
                throw new RobotException(ErrorCode.IMAGE_UPLOAD_CANT_FOUND_PATH.getMsg());
            }
            Friend friend = currentRobot.getFriends().get(0);
            Image image = ExternalResource.uploadAsImage(file, friend);
            //先存缓存
            TimedCachedUtil.getInstance().getImageCache().put(rawCode,image.getImageId());
            return  "[mirai:image:" + image.getImageId() + "]";
        }
    }
 }
