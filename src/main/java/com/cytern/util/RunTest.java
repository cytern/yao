package com.cytern.util;

import com.alibaba.fastjson.JSONObject;
import com.cytern.CommonMessageProcessor;

public class RunTest {
    public static void main(String[] args) {
        JSONObject rowData = new JSONObject();
        JSONObject subject = new JSONObject();
        subject.put("senderQQ","231231");
        rowData.put("subject",subject);
        rowData.put("message","阿斯我的好感度");
        try {
            CommonMessageProcessor commonMessageProcessor = new CommonMessageProcessor();
            commonMessageProcessor.handlerMessageEvent(rowData);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e);
        }
    }
}
