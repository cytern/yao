package com.cytern.util;

import net.mamoe.mirai.contact.Contact;

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
}
