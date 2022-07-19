package com.cytern.util;

import net.mamoe.mirai.contact.Contact;
import net.mamoe.mirai.contact.User;

/**
 * 信息发送工具
 */
public class MessageSenderUtil {

    /**
     * 基础信息发送
     */
    public static void normalSend(Contact subject,String finalMsg) {
//        subject.sendMessage(finalMsg);
        System.out.println(finalMsg);
    }

    /**
     * 处理mirai码
     */
    public static String uploadAndReplaceImage(String rawCode, User user) {
         return null;
    }
 }
