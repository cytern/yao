package com.cytern.network.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.LoggerService;
import com.cytern.service.impl.load.ConfigLoadService;

public class CommonHttpUtil {

    public static JSONObject postHttpService(String url,JSONObject params) {
        String requestBaseUrl = ConfigLoadService.getInstance().getDataService().getString("requestBaseUrl") + url;
        LoggerService.info("发送好感度HTTP请求 请求url: " + requestBaseUrl + "  请求参数:" + params.toJSONString());
        String returnData = HttpRequest.post(requestBaseUrl)
                .body(params.toJSONString())
                .execute().body();
        return JSONObject.parseObject(returnData);
    }
}
