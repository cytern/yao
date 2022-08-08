package com.cytern.util;

import com.alibaba.fastjson.JSONObject;
import com.cytern.CommonMessageProcessor;
import com.cytern.exception.NightStarNotFoundException;

public class StartMiraiUtil {

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sourceMessage","海丽乞巧");
        jsonObject.put("qqId","234123123");
        jsonObject.put("nickName","sdasdasda");
        CommonMessageProcessor commonMessageProcessor = new CommonMessageProcessor();
        commonMessageProcessor.handlerMessageEvent(jsonObject);
    }

}
