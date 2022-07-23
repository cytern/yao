package com.cytern.network.service;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSONObject;
import com.cytern.network.api.FavorFeign;
import com.cytern.service.impl.LoggerService;
import com.cytern.service.impl.load.ConfigLoadService;
import com.cytern.util.RobotCachedUtil;

/**
 * 好感度服务 拆分为了做缓存
 */
public class FavorService {
    public static Integer getFavor(Integer level,String qqId) {
        String key = qqId + ConfigLoadService.getInstance().getDefaultActiveRobot();
        String cachedLevel = RobotCachedUtil.getInstance().getFavorCache().get(key);
        if (cachedLevel != null) {
            Integer newLevel = Integer.parseInt(cachedLevel) + level;
            RobotCachedUtil.getInstance().getFavorCache().put(key, String.valueOf(newLevel));
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    JSONObject result = FavorFeign.favorEdit(level, qqId);
                }
            });
            return newLevel;
        }else {
            JSONObject result = FavorFeign.favorEdit(level, qqId);
            try {
                Integer newLevel = result.getJSONObject("data").getInteger("level");
                RobotCachedUtil.getInstance().getFavorCache().put(key, String.valueOf(newLevel));
                return newLevel;
            } catch (Exception e) {
                LoggerService.error("异常 好感度请求异常"+e.getMessage());
               return 0;
            }
        }
    }
}
