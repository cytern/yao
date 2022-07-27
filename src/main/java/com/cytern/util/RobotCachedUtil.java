package com.cytern.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.cache.impl.TimedCache;
import com.alibaba.fastjson.JSONObject;

public class RobotCachedUtil {
    private volatile static RobotCachedUtil robotCachedUtil;

    /**
     * 图片缓存 默认1小时过期
     */
    private final TimedCache<String, String> imageCache;

    /**
     * 好感度缓存 缓存1天
     */
    private final TimedCache<String,String> favorCache;
    /**
     * 物品类型缓存 缓存 最久未使用缓存
     */
    private final LRUCache<String, JSONObject> typeItemCache;
    private RobotCachedUtil() {
         imageCache = CacheUtil.newTimedCache(1000*60*60*1);
         favorCache = CacheUtil.newTimedCache(1000*60*60*24);
         typeItemCache = CacheUtil.newLRUCache(10);//存十个类型的物品
    }

    public static RobotCachedUtil getInstance() {
        if (robotCachedUtil == null) {
            synchronized (RobotCachedUtil.class) {
                if (robotCachedUtil == null) {
                    robotCachedUtil = new RobotCachedUtil();
                }
            }
        }
        return robotCachedUtil;
    }


    public TimedCache<String, String> getImageCache() {
        return imageCache;
    }
    public TimedCache<String, String> getFavorCache() {
        return favorCache;
    }

    public LRUCache<String, JSONObject> getTypeItemCache() {
        return typeItemCache;
    }
}


