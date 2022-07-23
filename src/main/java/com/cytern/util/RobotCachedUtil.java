package com.cytern.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;

public class RobotCachedUtil {
    private volatile static RobotCachedUtil robotCachedUtil;

    /**
     * 图片缓存 默认1小时过期
     */
    private TimedCache<String, String> imageCache;

    /**
     * 好感度缓存 缓存1天
     */
    private TimedCache<String,String> favorCache;
    private RobotCachedUtil() {
         imageCache = CacheUtil.newTimedCache(1000*60*60*1);
         favorCache = CacheUtil.newTimedCache(1000*60*60*24);
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

    public void setImageCache(TimedCache<String, String> imageCache) {
        this.imageCache = imageCache;
    }

    public TimedCache<String, String> getFavorCache() {
        return favorCache;
    }
}
