package com.cytern.util;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSONObject;
import com.cytern.exception.RobotException;
import com.cytern.pojo.ErrorCode;
import com.cytern.service.impl.load.base.AssetsUnzipLoadService;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Friend;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.utils.ExternalResource;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 * 信息发送工具
 */
public class MessageSenderUtil {

    /**
     * 基础信息发送
     */
    public static void normalSend(Contact subject, List<MessageChain> finalMsg) {
        for (MessageChain chain : finalMsg) {
            subject.sendMessage(chain);
        }
    }

    public static void fastSend(JSONObject command, String finalMsg) {
        MessageChainBuilder singleMessages = new MessageChainBuilder();
        singleMessages.append(finalMsg);
        Contact contact =(Contact) command.get("subject");
        contact.sendMessage(singleMessages.build());
    }

    /**
     * 处理mirai码
     */
    public static String uploadAndReplaceImage(String rawCode, Bot currentRobot) {
        //查看图片缓存是否有缓存的imageId
        String imageId = RobotCachedUtil.getInstance().getImageCache().get(rawCode);
        if (imageId != null) {
            return  imageId;
        }else {
            //从资产中获取
            String[] split = rawCode.split("\\.");
            if (split.length != 4) {
                throw new RobotException(ErrorCode.IMAGE_UPLOAD_CANT_FOUND_SOURCE.getMsg());
            }
            HashMap<String, HashMap<String, String>> assets = AssetsUnzipLoadService.getInstance().getAssets();
            HashMap<String, String> stringStringHashMap = assets.get(split[1]);
            if (stringStringHashMap == null) {
                throw new RobotException(ErrorCode.IMAGE_UPLOAD_CANT_FOUND_PATH.getMsg());
            }
            String fileName = split[2] + "." + split[3];
            String finalPath = stringStringHashMap.get(fileName);
            File file = FileUtil.file(finalPath);
            if (!file.exists()) {
                throw new RobotException(ErrorCode.IMAGE_UPLOAD_CANT_FOUND_PATH.getMsg());
            }
            ContactList<Friend> friends = currentRobot.getFriends();
            Image image = null;
            for (Friend friend : friends) {
                if (friend!= null) {
                    image = ExternalResource.uploadAsImage(file, friend);
                    break;
                }
            }
            //先存缓存
            RobotCachedUtil.getInstance().getImageCache().put(rawCode,image.getImageId());
            return  image.getImageId();
        }
    }

 }
