package com.cytern.util;

import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.LoggerService;

public class CommCodeResultUtil {
    /**
     * 获取最终的入参
     * @param params
     * @param arg2
     */
    public static void getFinalParams(JSONObject params, String arg2) {
        LoggerService.info("打印入参: "  );
        LoggerService.info("打印替换原始 " + arg2);
        if (arg2.contains("爻前置") || arg2.contains("爻入参") || arg2.contains("爻服务")) {
            String[] strings = arg2.split(".");
            JSONObject tempArgs = new JSONObject();
            tempArgs.putAll(params);
            for (int i = 0; i < strings.length; i++) {
                if (i == strings.length-1) {
                    arg2 = tempArgs.getString(strings[i]);
                    break;
                }else {
                    tempArgs = tempArgs.getJSONObject(strings[i]);
                }
            }
        }
    }
}
