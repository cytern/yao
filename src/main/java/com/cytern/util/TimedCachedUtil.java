package com.cytern.util;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;

public class TimedCachedUtil {
    private volatile static TimedCachedUtil timedCachedUtil;

    /**
     * 默认1小时过期
     */
    private TimedCache<String, String> imageCache;
    private TimedCachedUtil() {
         imageCache = CacheUtil.newTimedCache(1000*60*60*1);
    }

    public static TimedCachedUtil getInstance() {
        if (timedCachedUtil == null) {
            synchronized (TimedCachedUtil.class) {
                if (timedCachedUtil == null) {
                    timedCachedUtil = new TimedCachedUtil();
                }
            }
        }
        return timedCachedUtil;
    }


    public TimedCache<String, String> getImageCache() {
        return imageCache;
    }

    public void setImageCache(TimedCache<String, String> imageCache) {
        this.imageCache = imageCache;
    }
}
