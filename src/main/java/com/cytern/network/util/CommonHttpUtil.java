package com.cytern.network.util;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.cytern.service.impl.load.ConfigLoadService;

public class CommonHttpUtil {

    public static JSONObject postHttpService(String url,JSONObject params) {
        String returnData = HttpRequest.post(ConfigLoadService.getInstance().getDataService().getString("requestBaseUrl") + url)
                .body(params.toJSONString())
                .execute().body();
        return JSONObject.parseObject(returnData);
    }
}
