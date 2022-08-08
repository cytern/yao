package com.cytern.util;

import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.LoggerService;

public class CommCodeResultUtil {
    /**
     * 获取最终的入参
     * @param params
     * @param arg2
     */
    public static String getFinalParams(JSONObject params, String arg2) {
        if (arg2.contains("爻前置") || arg2.contains("爻入参") || arg2.contains("爻服务")) {
            String[] strings = arg2.split("\\.");
            JSONObject tempArgs = new JSONObject();
            tempArgs.putAll(params);
            for (int i = 0; i < strings.length; i++) {
                if (i == strings.length - 1) {
                    arg2 = tempArgs.getString(strings[i]);
                    break;
                } else {
                    tempArgs = tempArgs.getJSONObject(strings[i]);
                }
            }
            return arg2;
        }else {
            return arg2;
        }
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("返回结果","失败");
        jsonObject.put("爻前置1",jsonObject1);
        String finalParams = getFinalParams(jsonObject, "爻前置1.返回结果");
        System.out.println();
    }
}
